package adminApi.com.general.containers

import adminApi.com.database.DBManager
import adminApi.com.database.classes.DBEntity
import adminApi.com.database.services.ProducerEntity
import adminApi.com.general.models.CategoryElement
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ProducerData

class CategoryContainer(val db:DBManager)  : BaseContainer<CategoryElement, CategoryData>(db.categoryService()) {

    override val containerName = "CategoryContainer"

    override fun getDatabaseRecords(): List<DBEntity> {
        val result = db.producersService().select<ProducerEntity>()
        return result
    }

    override fun createElement():CategoryElement{
        return  CategoryElement()
    }

}

