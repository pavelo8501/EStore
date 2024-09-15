package po.estoreAdminApi.general.containers

import po.estoreAdminApi.database.DBManager
import po.estoreAdminApi.database.classes.DBEntity
import po.estoreAdminApi.database.services.CategoryEntity
import po.estoreAdminApi.general.models.CategoryElement
import po.estoreAdminApi.general.models.SupplierElement
import po.estoreAdminApi.general.models.data.CategoryData


class CategoryContainer(suppliers : List<SupplierElement>, val db: DBManager)  : BaseContainer<CategoryElement, CategoryData>(suppliers,db.categoryService()) {

    override val containerName = "CategoryContainer"

    override fun getDatabaseRecords(): List<DBEntity> {
        val result = db.categoryService().select<CategoryEntity>()
        return result
    }

    override fun createElement(): CategoryElement{
        return  CategoryElement()
    }

}

