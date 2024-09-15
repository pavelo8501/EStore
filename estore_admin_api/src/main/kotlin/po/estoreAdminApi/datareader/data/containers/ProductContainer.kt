package po.estoreAdminApi.datareader.data.containers


import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import po.estoreAdminApi.common.statistics.DataCategory
import po.estoreAdminApi.datareader.models.ProductDataModel
import po.estoreAdminApi.general.models.data.ICommonData


class ProductContainer(mappings: HashMap<String,String>, override var supplierId :Int ) : DataContainer<ProductDataModel>() {

   override var dataCategory = DataCategory.PRODUCT

   override fun createDataItem(source:JsonElement, mapping: HashMap<String,String>? ) : ProductDataModel {
        return ProductDataModel(source).also {
            it.supplierId = supplierId
        }
    }

   override suspend fun supplyData(dataItems : List<ICommonData>):Boolean{
       val products  =   dataItems.map { data ->  ProductDataModel().also { it.fromData(data) } }
       return setData(products,true)
    }

}