package adminApi.com.general.containers

import adminApi.com.database.DBManager
import adminApi.com.database.classes.DBEntity
import adminApi.com.database.services.CategoryEntity
import adminApi.com.database.services.ProducerEntity
import adminApi.com.general.models.CategoryElement
import adminApi.com.general.models.SupplierElement
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ProducerData

class CategoryContainer(suppliers : List<SupplierElement>, val db:DBManager)  : BaseContainer<CategoryElement, CategoryData>(suppliers,db.categoryService()) {

    override val containerName = "CategoryContainer"

    override fun getDatabaseRecords(): List<DBEntity> {
        val result = db.categoryService().select<CategoryEntity>()
        return result
    }

    override fun createElement():CategoryElement{
        return  CategoryElement()
    }

}

