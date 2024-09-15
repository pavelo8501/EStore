package po.estoreAdminApi.general.models

import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.IntEntity
import po.estoreAdminApi.database.classes.DBEntity
import po.estoreAdminApi.general.models.data.ICommonData


open class ElementClass() {

    var dbEntity: DBEntity? = null

    val  id: Int
        get() = dbEntity?.entityId  ?: 0

    val supplierId: Int
         get() = dbEntity?.supplierId ?: 0

    val providerId : String
        get() = dbEntity?.providerId ?: ""

    private var _markedForRemoval :Boolean = false
    var markedForRemoval  :Boolean
        get() = _markedForRemoval
        set(value) {
            if(value){
                onRemoveEntity?.invoke(dbEntity!!)
            }
            _markedForRemoval = value
        }


    var sendDataObjectToReader : ((ICommonData) -> Unit)? = null
    var onUpdateEntity : ((DBEntity, ICommonData) -> Unit)? = null
    var onRemoveEntity : ((DBEntity) -> Unit)? = null


    init {
       this._markedForRemoval = dbEntity?.markedForRemovalAt != null
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

            val entityData = this.dbEntity!!.toData()

            if(entityData == data){
               val a = 10
            }else{
               this.onUpdateEntity?.invoke(this.dbEntity!!, data)
               val a = 10
            }
        }
    }
}