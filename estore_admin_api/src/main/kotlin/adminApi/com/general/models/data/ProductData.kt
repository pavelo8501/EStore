package adminApi.com.general.models.data
import kotlinx.serialization.Serializable


@Serializable
data class ProductData(
    override var id :Int,
    override var supplierId :Int,
    override var providerId: String,
    var producerId : String,
    var producerCode : String ,
    var categoriesIds : String,
    var name: String,
    var quantity: Int,
    var price: Double,
    var currency: String,
    var ean: String,
    var sizeX: Int,
    var sizeY: Int,
    var sizeZ: Int,
    var weight : Int,
    var additionalParameters : String
): ICommonData{
    override var markedForRemoval: Boolean = false
}