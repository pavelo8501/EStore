package adminApi.com.datareader.data.containers

import adminApi.com.datareader.models.ProducerDataModel
import adminApi.com.datareader.models.ProductDataModel
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.ProducerData
import adminApi.com.general.models.data.ProductData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


class ProductContainer(mappings: HashMap<String,String>, override var supplierId :Int ) : DataContainer<ProductDataModel>() {

   override fun createDataItem(source:JsonElement, mapping: HashMap<String,String>? ) : ProductDataModel {
        return ProductDataModel(source).also {
            it.supplierId = supplierId
        }
    }

    fun addProducts(dataItems : List<ProductData>){
        val products  =   dataItems.map { data ->  ProductDataModel().also { it.fromData(data) } }
        setData(products,true)
    }

}