package adminApi.com.datareader.data.containers

import adminApi.com.common.dataflow.*
import adminApi.com.common.statistics.*
import adminApi.com.datareader.data.DataProvider
import adminApi.com.datareader.data.DataProvider.MeterCompanion
import adminApi.com.datareader.models.abstractions.DataModelClass
import adminApi.com.general.models.data.ICommonData
import kotlinx.coroutines.*
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


abstract class DataContainer<T:DataModelClass>(): DataFlowMeter {

    companion object MeterCompanion : DataFlowMeterCompanion {
        override var parent: Any? = null
        override var objectName: String = "Object Name"
        override var module: ModuleName = ModuleName.DATAREADER
        override var supplierName: String = ""
        override val measurements: MutableList<IMeasurableCallResult> = mutableListOf()

        override val onDispatcherResultsSubmitted = { result: ServiceCallResult -> this.registerMeasurement(result) }
        override val onMeterResultsSubmitted: ((MeasurableCallResult) -> Unit)? =
            { result: MeasurableCallResult -> this.registerMeasurement(result.operation, result.elapsedTime) }

        override fun registerMeasurement(serviceCallResult: ServiceCallResult) {
            if (serviceCallResult.success) {
                val newRecord = MeasurableCallResultImpl(module, objectName)
                newRecord.count = serviceCallResult.count
                this.measurements.add(newRecord)
            }
        }

        override fun registerMeasurement(operation: String, elapsedTime: Long) {
            this.measurements.firstOrNull { it.operation == operation }?.elapsedTime = elapsedTime
        }

        fun getLastMeasurement(category: DataCategory): IMeasurableCallResult? {
            val first = measurements.firstOrNull{it.unitName == category}
            return first
        }
    }
    private val dataDispatcher: DataDispatcherImpl = DataDispatcherImpl()

    abstract var supplierId:Int
    abstract var containerType : ContainerType
    private var busy = false
        set(value) {
            if(field != value) {
                field = value
                dataDispatcher.setRecipientBusyStatus(field)
            }

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

    var companion: DataFlowMeterCompanion = DataContainer.MeterCompanion
    override fun initCompanion() {
        companion.init("DataProvider", ModuleName.DATAREADER, "Action", this)
        dataDispatcher.init(DataProvider.MeterCompanion.onDispatcherResultsSubmitted)
    }

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
            } else {
                this.records[index].fromDataManager = false
            }
        }
    }

    suspend fun setDataFromSource(dataContainer: DataFlowContainerImpl) = dataDispatcher.receiveData<T>(dataContainer, this.busy, {
            this.onBuffered = {
                println("Buffered")
            }
            this.onRelease = {
                beforeUpdate()
                forEach {
                    addRecord((it as T))
                }
                afterUpdate()
            }
        }) {

                 startMeasure<List<T>>("lol",FlowType.UPDATE,MeterCompanion.onMeterResultsSubmitted){
                        val jasonList: List<JsonElement> = dataContainer.dataList.filterIsInstance<JsonElement>()
                        val itemList = jasonList.map { createDataItem(it, mapping) }
                        itemList
                    }
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
        data.forEach {
            it.fromDataManager = fromDataManager
            this.addRecord(it)
        }
        afterUpdate()
        return true
    }

    fun getRecords(): List<T>{
        return this.records
   }

}