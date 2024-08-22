package adminApi.com.datareader.data.containers

import adminApi.com.datareader.models.ProducerDataModel
import adminApi.com.datareader.models.abstractions.DataModelClass
import adminApi.com.general.models.data.ICommonData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement

abstract class DataContainer<T:DataModelClass>(){

   abstract var supplierId:Int
   var mapping: HashMap<String,String>? = null
   private val jsonItems : MutableList<JsonElement> = mutableListOf()
   private val records : MutableList<T> = mutableListOf()
   private val toSaveBuffer : MutableList<T> = mutableListOf()
   var onDataUpdated: ((List<T>)->Unit)? = null

   fun onDataUpdated(function: () -> Unit): Any = Unit

    abstract fun createDataItem(source:JsonElement, mapping: HashMap<String,String>? = null) : T

    private fun sendForUpdate(){
        if(onDataUpdated!=null){
            if(this.toSaveBuffer.isNotEmpty()){
                onDataUpdated?.invoke(this.toSaveBuffer)
                this.toSaveBuffer.clear()
            }
        }
    }

    private fun getExistentIfDataChanged(record: T):T?{
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

    private fun addRecord(record: T){
        val index = this.records.indexOfFirst {it.providerId == record.providerId }
        if(index < 0){
            this.records.add(record)
            if(!record.fromDataManager){
                this.toSaveBuffer.add(record)
            }
        }else {
            val existent = getExistentIfDataChanged(record)
            if (existent != null) {
                if (existent.fromDataManager) {
                    record.id = existent.id
                    this.toSaveBuffer.add(record)
                    this.replaceRecord(existent, record)
                } else {
                    this.toSaveBuffer.add(existent)
                }
            }
        }
   }

    fun setData(data:List<JsonElement>){
        this.jsonItems.clear()
        this.jsonItems.addAll(data)
        for(record in jsonItems){
            val dataItem = this.createDataItem(record,mapping)
            this.addRecord(dataItem)
        }
       sendForUpdate()
   }

    fun setData(data : List<T>, fromDataManager : Boolean = true ){
        try {
            data.forEach { item->
                item.fromDataManager = fromDataManager
                this.addRecord(item)
            }
        }catch (ex  :Exception){
            throw Exception("Item creation failed "+ ex.message)
        }
        sendForUpdate()
    }

    fun getData(): List<T>{
        return this.records
   }

}