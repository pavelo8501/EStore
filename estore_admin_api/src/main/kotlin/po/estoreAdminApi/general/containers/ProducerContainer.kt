package po.estoreAdminApi.general.containers

import po.estoreAdminApi.database.DBManager
import po.estoreAdminApi.database.classes.DBEntity
import po.estoreAdminApi.database.services.ProducerEntity
import po.estoreAdminApi.general.models.ProducerElement
import po.estoreAdminApi.general.models.SupplierElement
import po.estoreAdminApi.general.models.data.ProducerData


class ProducersContainer(suppliers : List<SupplierElement>, val db: DBManager)  : BaseContainer<ProducerElement, ProducerData>(suppliers,db.producersService()) {

    override val containerName = "ProducersContainer"

    override fun getDatabaseRecords(): List<DBEntity> {
        val result = db.producersService().select<ProducerEntity>()
        return result
    }

    override fun createElement():ProducerElement{
        return  ProducerElement()
    }

}

