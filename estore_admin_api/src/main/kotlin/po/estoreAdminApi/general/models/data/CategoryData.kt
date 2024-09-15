package po.estoreAdminApi.general.models.data
import kotlinx.serialization.Serializable

@Serializable
data class CategoryData(
    override var id :Int,
    override var supplierId :Int,
    override var providerId: String,
    var parentCategoryId : Int,
    var name: String
): ICommonData{
    override var markedForRemoval: Boolean = false
}