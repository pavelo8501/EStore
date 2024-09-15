package po.estoreAdminApi.general.models

import po.estoreAdminApi.database.services.CategoryEntity

class CategoryElement() : ElementClass()  {

    private val categoryEntity :  CategoryEntity?
         get() = super.dbEntity as CategoryEntity?

}