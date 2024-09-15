package po.estoreAdminApi.datareader.models

import kotlinx.serialization.json.JsonElement
import po.estoreAdminApi.datareader.models.abstractions.DataModelClass
import po.estoreAdminApi.general.models.data.ICommonData
import po.estoreAdminApi.general.models.data.ProductData

class ProductDataModel(jsonElement:JsonElement? = null) : DataModelClass(jsonElement) {

    override var providerId: String = strProp("productId")
    var producerId : String = strProp("producerId")
    var producerCode : String = strProp("producerCode")
    var categoriesIds : String = strProp("categoriesIds",true)
    var name: String = strProp("productName")
    var quantity: Int = intProp("quantity")
    var price: Double = doubleProp("price")
    var currency: String = strProp("currency")
    var ean: String = strProp("ean")
    var sizeX: Int = intProp("sizeX")
    var sizeY: Int = intProp("sizeY")
    var sizeZ: Int = intProp("sizeZ")
    var weight : Int = intProp("weight")
    var additionalParameters : String = strProp("additionalParameters",true)

    override fun toData(): ProductData{
        return  ProductData(
            this.id,
            this.supplierId,
            this.providerId,
            this.producerId,
            this.producerCode,
            this.categoriesIds,
            this.name,
            this.quantity,
            this.price,
            this.currency,
            this.ean,
            this.sizeX,
            this.sizeY,
            this.sizeZ,
            this.weight,
            this.additionalParameters)
    }

    override fun fromData(data: ICommonData){
        if(data is ProductData){
            this.id = data.id
            this.supplierId = data.supplierId
            this.providerId = data.providerId
            this.producerId = data.producerId
            this.producerCode = data.producerCode
            this.categoriesIds = data.categoriesIds
            this.name = data.name
            this.quantity = data.quantity
            this.price = data.price
            this.currency = data.currency
            this.ean = data.ean
            this.sizeX = data.sizeX
            this.sizeY = data.sizeY
            this.sizeZ = data.sizeZ
            this.weight = data.weight
            this.additionalParameters = data.additionalParameters
        }
        this.markedForRemoval = data.markedForRemoval
    }

    init {
        val a = 10
    }

}