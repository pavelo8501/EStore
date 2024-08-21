package adminApi.com.general.containers

import adminApi.com.database.classes.DBEntity
import adminApi.com.database.services.DatabaseService
import adminApi.com.general.models.ElementClass
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProducerData

abstract class BaseContainer<T:ElementClass, D:ICommonData>(val dbService : DatabaseService ) {

    abstract val containerName :String
    abstract fun createElement():T
    abstract fun getDatabaseRecords(): List<DBEntity>

    protected var elementList : MutableList<T> = mutableListOf()
    private var recordsUpdated = false
    private val dataRecordBuffer: MutableList<D> = mutableListOf()

    var onSendDataToReader : ((List<D>, containerName: String)->Unit)? = null

    protected fun updateEntity(entity : DBEntity, data : ICommonData){
        dbService.dbQuery {
           entity.fromData(data)
           entity.flush()
        }
    }

    private fun bindToEntityCallbacks(element: T) {
       // @Suppress("UNCHECKED_CAST")
        element.sendDataObjectToReader = { data -> dataRecordBuffer.add((data as D))  }
        element.onUpdateEntity = { entity, data -> this.updateEntity(entity, data) }
    }

    fun getElements(): List<T>{
        return this.elementList.toList()
    }

    fun clearBuffer(){
        dataRecordBuffer.clear()
    }
    fun getBufferedData():List<D>{
        val result = dataRecordBuffer.toList()
        return  result
    }

    fun addElement(element: T, dbEntity : DBEntity) {
        this.elementList.firstOrNull{ it.id == dbEntity.entityId }.let {
            if(it == null){
                bindToEntityCallbacks(element)
                element.initFromEntity(dbEntity)
                this.elementList.add(element)
            }else{
                it.initFromEntity(dbEntity)
            }
        }
    }

    fun processDataFromDataProvider(supplierId:Int, records : List<ICommonData>){
        if(this.recordsUpdated == false){
            println("Writer supplied data before database loaded")
            return
        }
        records.forEach {  record-> run {
            elementList.firstOrNull{  it.providerId == record.providerId && it.supplierId == supplierId  }.let { element ->
                if(element != null){
                    record.id = element.id
                    element.initFromData(record)
                }else{
                    dbService.create(record).let {
                        val newElement = createElement()
                        addElement(newElement, it)
                    }
                }
            }}
        }
      //  deleteRedundant(supplierId ,records)
    }

    fun deleteRedundant(supplierId:Int ,  records : List<ICommonData>){
        val redundantList : MutableList<T> = mutableListOf()
        this.elementList.filter { it.supplierId == supplierId }
            .forEach { element-> records
                .firstOrNull{ it.providerId == element.providerId }?: redundantList.add(element)
            }
        dbService.dbQuery {
            redundantList.forEach{
                try {
                    it.dbEntity!!.delete()
                    elementList.remove(it)
                }catch (ex : Exception){
                    throw Exception("Delete Failed for entity id ${it.id}")
                }
            }
        }
    }

    fun init(){
        try {
            val result = this.getDatabaseRecords()
            for (entity in result) {
                try {
                    val newElement = createElement()
                    addElement(newElement, entity)
                }catch (e:Exception){
                    println(e.message)
                }
            }
            this.recordsUpdated = true
            if(this.dataRecordBuffer.isNotEmpty()){
                onSendDataToReader?.invoke(this.dataRecordBuffer.toList(),containerName)
                this.dataRecordBuffer.clear()
            }

        }catch (ex : Exception){
            this.recordsUpdated = false
            throw Exception("Could not process records from database")
        }
    }

}

