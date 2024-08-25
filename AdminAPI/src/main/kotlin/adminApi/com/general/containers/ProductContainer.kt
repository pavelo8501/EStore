package adminApi.com.general.containers


import adminApi.com.database.DBManager
import adminApi.com.database.classes.DBEntity
import adminApi.com.database.services.ProductEntity
import adminApi.com.general.models.ProductElement
import adminApi.com.general.models.SupplierElement
import adminApi.com.general.models.data.ProducerData
import adminApi.com.general.models.data.ProductData

class ProductContainer(suppliers : List<SupplierElement>, val db:DBManager)  : BaseContainer<ProductElement, ProductData>(suppliers,db.productService()) {

    override val containerName = "ProductContainer"

    override fun getDatabaseRecords(): List<DBEntity> {
        val result = db.productService().select<ProductEntity>()
        return result
    }

    override fun createElement():ProductElement{
        return  ProductElement()
    }

}

