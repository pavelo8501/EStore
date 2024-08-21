package adminApi.com.datareader.data.containers

import adminApi.com.datareader.models.CategoryDataModel
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.CategoryData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


class CategoryContainer(mappings: HashMap<String,String>, override var supplierId :Int ) : DataContainer<CategoryDataModel>() {

   override fun createDataItem(source:JsonElement, mapping: HashMap<String,String>? ) : CategoryDataModel {
        return CategoryDataModel(JsonPropertyConverter(source)).also {
            it.supplierId = supplierId
            it.converter.setProviderData(source,mapping)
        }
    }

    fun addCategory(dataItem : CategoryData){
        addData(Json.encodeToJsonElement(CategoryData.serializer(),dataItem))
    }

}