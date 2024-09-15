package po.estoreAdminApi.general.containers

import po.estoreAdminApi.database.classes.DBEntity
import po.estoreAdminApi.database.services.DatabaseService
import po.estoreAdminApi.general.models.ElementClass
import po.estoreAdminApi.general.models.SupplierElement
import po.estoreAdminApi.general.models.data.ICommonData


class DataContainerUpdate<D>(
    var supplierId : Int,
    var records : List<D>
    )


abstract class BaseContainer<T: ElementClass, D: ICommonData>(val suppliers : List<SupplierElement>, val dbService : DatabaseService ) {

    abstract val containerName :String
    abstract fun createElement():T
    abstract fun getDatabaseRecords(): List<DBEntity>

    protected var elementList : MutableList<T> = mutableListOf()
    private var recordsUpdated = false
    private val dataRecordBuffer: MutableList<D> = mutableListOf()

    var onSendDataToReader : ((DataContainerUpdate<D>)->Unit)? = null

    //var onSendDataToReader : ((List<D>, containerName: String)->Unit)? = null

    protected fun updateEntity(entity : DBEntity, data : ICommonData){
        dbService.dbQuery {
           entity.fromData(data)
           entity.flush()
        }
    }

    protected fun markDeleteEntity(entity : DBEntity){
        dbService.dbQuery {
            entity.markedForRemoval = true
        }
    }

    private fun bindToEntityCallbacks(element: T) {
       // @Suppress("UNCHECKED_CAST")
        element.sendDataObjectToReader = { data -> dataRecordBuffer.add((data as D))  }
        element.onUpdateEntity = { entity, data -> this.updateEntity(entity, data) }
        element.onRemoveEntity = { entity -> this.markDeleteEntity(entity)}
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
    }

    fun processRemovalListFromDataProvider(supplierId:Int, records : List<ICommonData>){
        if(!this.recordsUpdated){
            println("Writer supplied data before database loaded")
            return
        }
        records.forEach {  record-> run {
            elementList.firstOrNull{  it.providerId == record.providerId && it.supplierId == supplierId  }.let { element ->
                if(element != null){
                   element.markedForRemoval = true
                }
            }}
        }

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

    suspend fun init(){
        try {
            for(supplier in suppliers){
                val result = dbService.dbQuery {
                    dbService.select<DBEntity>()
                }
                for (entity in result) {
                    try {
                        val newElement = createElement()
                        addElement(newElement, entity)
                    }catch (e:Exception){
                        println(e.message)
                    }
                }
            }

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
            val recordsBySupplier = this.dataRecordBuffer.groupBy { it.supplierId }
            if(recordsBySupplier.isNotEmpty()) {
                recordsBySupplier.forEach { (supplierId, records) -> if(records.isNotEmpty()){ onSendDataToReader?.invoke(DataContainerUpdate(supplierId,records)) } }
                this.dataRecordBuffer.clear()
            }
//            else{
//                suppliers.forEach { supplier ->   onSendDataToReader?.invoke(DataContainerUpdate(supplier.supplierId, mutableListOf()))  }
//            }
        }catch (ex : Exception){
            this.recordsUpdated = false
            throw Exception("Could not process records from database")
        }
    }

}

