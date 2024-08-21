package adminApi.com.general.models

import adminApi.com.database.classes.DBEntity
import adminApi.com.database.services.ProducerEntity
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProducerData
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity



open class ElementClass() {

    var dbEntity: DBEntity? = null

    val  id: Int
        get() = dbEntity?.entityId  ?: 0

    val supplierId: Int
         get() = dbEntity?.supplierId ?: 0

    val providerId : String
        get() = dbEntity?.providerId ?: ""

    var sendDataObjectToReader : ((ICommonData) -> Unit)? = null
    var onUpdateEntity : ((DBEntity, ICommonData) -> Unit)? = null


    init {
       //sendDataObjectToReader?.invoke(this.dbEntity.toModel())
    }

    private fun compareEntity(entity: DBEntity) : Boolean{
        val dataObject  =  entity.toData()
        return dataObject == this.dbEntity?.toData()
    }

    fun initFromEntity(entity: DBEntity){
        if(this.dbEntity == null) {
            this.dbEntity = entity
             sendDataObjectToReader?.invoke(entity.toData())
        }else{
            val equal = compareEntity(entity)
            if(!equal){
                this.dbEntity = entity
            }
        }
    }

    fun initFromData(data: ICommonData){
        if(this.dbEntity != null){
            if(this.dbEntity!!.toData() == data){
               val a = 10
            }else{
               this.onUpdateEntity?.invoke(this.dbEntity!!, data)
               val a = 10
            }
        }
    }
}