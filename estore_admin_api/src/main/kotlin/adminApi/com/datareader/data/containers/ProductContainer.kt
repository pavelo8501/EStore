package adminApi.com.datareader.data.containers

import adminApi.com.common.statistics.DataCategory
import adminApi.com.datareader.models.ProducerDataModel
import adminApi.com.datareader.models.ProductDataModel
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProducerData
import adminApi.com.general.models.data.ProductData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


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