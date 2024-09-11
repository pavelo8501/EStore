package adminApi.com.general.models

import adminApi.com.database.services.ProductEntity

class ProductElement() : ElementClass()  {

    private val productEntity :  ProductEntity?
         get() = super.dbEntity as ProductEntity?


}