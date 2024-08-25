package adminApi.com.datareader.data.containers

import adminApi.com.common.dataflow.DataDispatcherImpl
import adminApi.com.common.statistics.CallResultImpl
import adminApi.com.common.statistics.DataCategory
import adminApi.com.common.statistics.DataType
import adminApi.com.common.statistics.ServiceCallResult
import adminApi.com.datareader.models.abstractions.DataModelClass
import adminApi.com.general.models.data.ICommonData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlin.coroutines.CoroutineContext
import kotlin.math.E


enum class ContainerType{
    PRODUCER,
    PRODUCT,
    CATEGORY
}

class AccessReleaseTimer(attempts: Int = 20, delay: Int = 5000){

    val coroutine = CoroutineScope(Dispatchers.Default)

    suspend fun <T> start(busy:Boolean, onCancel: T.()->Unit){

    }
}

class ContainerResultImpl(
    override val success: Boolean,
    override val dataType: DataType,
    override val dataList: List<*>,
    override val count: Int,
    override val errorCode: Int? = null,
    override val errorMessage: String? = null,
) : ServiceCallResult{

}

abstract class DataContainer<T:DataModelClass>(){

    val dataDispatcher: DataDispatcherImpl = DataDispatcherImpl()

   abstract var supplierId:Int
   abstract var containerType : ContainerType
   private var busy = true
       get() = field
         set(value) {
              field = value
         }
   private var completeDataUploadFromSupplier = false
   private var initialUpdate = false
   var mapping: HashMap<String,String>? = null
   private val jsonItems : MutableList<JsonElement> = mutableListOf()
   private val records : MutableList<T> = mutableListOf()
   private var toSaveBuffer : MutableList<T> = mutableListOf()
   var onDataForUpdated: ((List<T>)->Unit)? = null
   var onDataForRemoval : ((List<T>)->Unit)? = null

    abstract fun createDataItem(source:JsonElement, mapping: HashMap<String,String>? = null) : T
    abstract suspend fun supplyData(dataItems : List<ICommonData>):Boolean

    private fun sendRedundantForRemoval(){
        if(this.completeDataUploadFromSupplier && !this.initialUpdate){
            val redundantList = this.records.filter { it.fromDataManager && !it.markedForRemoval }
            if(redundantList.isNotEmpty()){
                onDataForRemoval?.invoke(redundantList)
            }
        }
    }

    private fun sendForUpdate(){
        if(onDataForUpdated!=null){
            if(this.toSaveBuffer.isNotEmpty()){
                onDataForUpdated?.invoke(this.toSaveBuffer)
                this.toSaveBuffer = mutableListOf()
            }
        }
    }

    private fun getExistentIfDataDiffer(record: T):T?{
        val index = this.records.indexOfFirst {it.providerId == record.providerId }
        val newRecordData = record.toData()
        newRecordData.id = 0
        val foundRecordData = this.records[index].toData()
        foundRecordData.id = 0
        return if(newRecordData == foundRecordData) {
            null
        }else{
            this.records[index]
        }
    }

    private fun replaceRecord(oldRecord: T, newRecord: T){
        val index = this.records.indexOfFirst {it.providerId == oldRecord.providerId }
        if(index >= 0){
            this.records[index] = newRecord
        }
    }

    private fun beforeUpdate(){
        busy = true
    }

    private fun afterUpdate(){
        sendForUpdate()
        sendRedundantForRemoval()
        this.initialUpdate = false
        busy = false
    }

    private fun addRecord(record: T) {

        if(this.initialUpdate){
            this.records.add(record)
            return
        }

        val index = this.records.indexOfFirst { it.providerId == record.providerId }
        if (index < 0) {
            this.records.add(record)
            if (!record.fromDataManager) {
                this.toSaveBuffer.add(record)
            }
        } else {
            val existent = getExistentIfDataDiffer(record)
            if (existent != null) {
                if (existent.fromDataManager) {
                    record.id = existent.id
                    this.toSaveBuffer.add(record)
                    this.replaceRecord(existent, record)
                } else {
                    this.toSaveBuffer.add(existent)
                }
            }else{
                this.records[index].fromDataManager = false
            }
        }
    }

    suspend fun setDataFromSource(data: List<JsonElement>, dataComplete: Boolean = false) = dataDispatcher.setData<List<JsonElement>, DataContainer<T>>(data, dataComplete, this.busy, {
        this.onBuffered = {
            println("Buffered")
        }
        this.onBufferRelease = {
            println("Released")
        }
    }) {

        beforeUpdate()
       // this.completeDataUploadFromSupplier = dataComplete
        this.jsonItems.clear()
        this.jsonItems.addAll(data)
        for(record in jsonItems){
            val dataItem = this.createDataItem(record,mapping)
            this.addRecord(dataItem)
        }
        val result = ContainerResultImpl(true, DataType.JSON, listOf<T>(),0)
        afterUpdate()
//        var attemps = 20
//        while (busy){
//            delay(5000)
//            attemps--
//            if (attemps == 0){
//                throw Exception("Data container is busy")
//            }
//        }

   }

    suspend fun setData(data : List<T>, fromDataManager : Boolean = true ):Boolean{

        var attemps = 20
        while (busy){
            delay(5000)
            attemps--
            if (attemps == 0){
                return false
            }
        }
        beforeUpdate()
        data.forEach { item->
            item.fromDataManager = fromDataManager
            this.addRecord(item)
        }
        afterUpdate()
        return true
    }

    fun getRecords(): List<T>{
        return this.records
   }

}