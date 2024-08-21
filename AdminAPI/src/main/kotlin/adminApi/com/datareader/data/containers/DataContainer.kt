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

   private fun checkIfDataChanged(record: T):Boolean{
       val index = this.records.indexOfFirst {it.providerId == record.providerId }
       if(index < 0){
           this.toSaveBuffer.add(record)
           return false
       }else{
           if(record.toData() !=  this.records[index].toData()){
               this.toSaveBuffer.add(record)
               this.records[index] = record

               return true
           }else{
               this.records[index].fromDataManager = false
               val a =10
               return true
           }
       }
   }
   private fun addRecord(record: T){
       if(!checkIfDataChanged(record)){
           this.records.add(record)
       }
   }

   fun setData(data:List<JsonElement>){
        this.jsonItems.clear()
        this.jsonItems.addAll(data)
        for(record in jsonItems){
            val dataItem = this.createDataItem(record,mapping)
            this.addRecord(dataItem)
        }
       if(this.toSaveBuffer.isNotEmpty()){
           onDataUpdated?.invoke(this.toSaveBuffer)
           this.toSaveBuffer.clear()
       }
   }
   fun addData(data : ICommonData ){
       try {
           val jsonData = Json.encodeToJsonElement(data)
           val newDataItem = this.createDataItem(jsonData, mapping)
           newDataItem.id = data.id
           newDataItem.supplierId = data.supplierId
           newDataItem.fromDataManager = true
           this.addRecord(newDataItem)
       }catch (ex  :Exception){
           throw Exception("Item creation failed "+ ex.message)
       }
   }

   fun getData(): List<T>{
        return this.records
   }

//   fun <F> findRecord(id:Int): DataModelClass?{
//        return this.records.find { it.supplierId == id } as DataModelClass?
//    }


}