package adminApi.com.datareader.models

import adminApi.com.datareader.models.abstractions.DataModelClass
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProducerData
import kotlinx.serialization.json.JsonElement

class CategoryDataModel(jsonElement:JsonElement? = null) : DataModelClass(jsonElement) {

    override var providerId: String = strProp("categoryId")
    var parentCategoryId : Int = intProp("parentCategoryId")
    var name: String = strProp("name")

    override fun toData():CategoryData{
        return  CategoryData(this.id, this.supplierId, this.providerId, parentCategoryId,this.name)
    }


    override fun fromData(data: ICommonData) {
        if (data is CategoryData) {
            this.id = data.id
            this.supplierId = data.supplierId
            this.providerId = data.providerId
            this.parentCategoryId = data.parentCategoryId
            this.name = data.name
        }
        this.markedForRemoval = data.markedForRemoval
    }

}