package po.estoreAdminApi.general.models.data
import kotlinx.serialization.Serializable


@Serializable
data class ProducerData(
    override var id :Int,
    override var supplierId :Int,
    override var providerId: String,
    var name: String = ""
): ICommonData{
    override var markedForRemoval: Boolean = false
}