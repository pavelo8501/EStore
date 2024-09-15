package po.estoreAdminApi.general.models

import po.estoreAdminApi.database.services.ProductEntity

class ProductElement() : ElementClass()  {

    private val productEntity :  ProductEntity?
         get() = super.dbEntity as ProductEntity?


}