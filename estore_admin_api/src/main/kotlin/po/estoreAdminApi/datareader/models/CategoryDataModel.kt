package po.estoreAdminApi.datareader.models


import kotlinx.serialization.json.JsonElement
import po.estoreAdminApi.datareader.models.abstractions.DataModelClass
import po.estoreAdminApi.general.models.data.CategoryData
import po.estoreAdminApi.general.models.data.ICommonData

class CategoryDataModel(jsonElement:JsonElement? = null) : DataModelClass(jsonElement) {

    override var providerId: String = strProp("categoryId")
    var parentCategoryId : Int = intProp("parentCategoryId")
    var name: String = strProp("name")

    override fun toData(): CategoryData{
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