package adminApi.com.general.models

import adminApi.com.database.services.CategoryEntity

class CategoryElement() : ElementClass()  {

    private val categoryEntity :  CategoryEntity?
         get() = super.dbEntity as CategoryEntity?


}