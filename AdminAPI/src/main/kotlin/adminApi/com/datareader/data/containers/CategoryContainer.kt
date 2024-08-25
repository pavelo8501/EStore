package adminApi.com.datareader.data.containers

import adminApi.com.datareader.models.CategoryDataModel
import adminApi.com.datareader.models.ProducerDataModel
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProducerData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement


class CategoryContainer(mappings: HashMap<String,String>, override var supplierId :Int ) : DataContainer<CategoryDataModel>() {

  override var containerType = ContainerType.CATEGORY

   override fun createDataItem(source:JsonElement, mapping: HashMap<String,String>? ) : CategoryDataModel {
        return CategoryDataModel(source).also {
            it.supplierId = supplierId
        }
    }

    override suspend fun supplyData (dataItems : List<ICommonData>):Boolean{
        val categories  =  dataItems.map { data ->  CategoryDataModel().also { it.fromData(data) } }
        return setData(categories,true)
    }


}