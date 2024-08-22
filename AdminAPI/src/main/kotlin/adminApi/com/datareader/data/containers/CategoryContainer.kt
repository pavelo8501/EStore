package adminApi.com.datareader.data.containers

import adminApi.com.datareader.models.CategoryDataModel
import adminApi.com.datareader.models.ProducerDataModel
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ProducerData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


class CategoryContainer(mappings: HashMap<String,String>, override var supplierId :Int ) : DataContainer<CategoryDataModel>() {

   override fun createDataItem(source:JsonElement, mapping: HashMap<String,String>? ) : CategoryDataModel {
        return CategoryDataModel(source).also {
            it.supplierId = supplierId
        }
    }

    fun addCategories(dataItems : List<CategoryData>){
        val categories  =  dataItems.map { data ->  CategoryDataModel().also { it.fromData(data) } }

        setData(categories,true)
    }

}