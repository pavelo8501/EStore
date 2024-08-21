package adminApi.com.datareader.models

import adminApi.com.datareader.models.abstractions.DataModelClass
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ProducerData

class CategoryDataModel(val converter  : JsonPropertyConverter) : DataModelClass() {
    override var providerId: String = converter.strProp("categoryId")
    var parentCategoryId : Int = converter.intProp("parentCategoryId")
    var name: String = converter.strProp("name")

    override fun toData():CategoryData{
        return  CategoryData(this.id, this.supplierId, this.providerId, parentCategoryId,this.name)
    }

}