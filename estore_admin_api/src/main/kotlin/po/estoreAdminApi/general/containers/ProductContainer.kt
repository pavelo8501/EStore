package po.estoreAdminApi.general.containers

import po.estoreAdminApi.database.DBManager
import po.estoreAdminApi.database.classes.DBEntity
import po.estoreAdminApi.database.services.ProductEntity
import po.estoreAdminApi.general.models.ProductElement
import po.estoreAdminApi.general.models.SupplierElement
import po.estoreAdminApi.general.models.data.ProductData


class ProductContainer(suppliers : List<SupplierElement>, val db: DBManager)  : BaseContainer<ProductElement, ProductData>(suppliers,db.productService()) {

    override val containerName = "ProductContainer"

    override fun getDatabaseRecords(): List<DBEntity> {
        val result = db.productService().select<ProductEntity>()
        return result
    }

    override fun createElement():ProductElement{
        return  ProductElement()
    }

}

