package po.estoreAdminApi.datareader.data.containers


import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import po.estoreAdminApi.common.statistics.DataCategory
import po.estoreAdminApi.datareader.models.CategoryDataModel
import po.estoreAdminApi.general.models.data.ICommonData


class CategoryContainer(mappings: HashMap<String,String>, override var supplierId :Int ):DataContainer<CategoryDataModel>() {

    override var dataCategory = DataCategory.CATEGORY

    override fun createDataItem(source:JsonElement, mapping: HashMap<String,String>? ) : CategoryDataModel {
        return CategoryDataModel(source).also {
            it.supplierId = supplierId
        }
    }

    override suspend fun supplyData (dataItems : List<ICommonData>):Boolean{
        val categories = dataItems.map { data ->  CategoryDataModel().also { it.fromData(data) } }
        return setData(categories,true)
    }
}