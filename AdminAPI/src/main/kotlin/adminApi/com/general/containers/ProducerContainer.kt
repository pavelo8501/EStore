package adminApi.com.general.containers


import adminApi.com.database.DBManager
import adminApi.com.database.classes.DBEntity
import adminApi.com.database.services.ProducerEntity
import adminApi.com.general.models.ProducerElement
import adminApi.com.general.models.data.ProducerData

class ProducersContainer(val db:DBManager)  : BaseContainer<ProducerElement, ProducerData>(db.producersService()) {

    override val containerName = "ProducersContainer"

    override fun getDatabaseRecords(): List<DBEntity> {
        val result = db.producersService().select<ProducerEntity>()
        return result
    }

    override fun createElement():ProducerElement{
        return  ProducerElement()
    }

}

