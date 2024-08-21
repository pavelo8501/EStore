package adminApi.com.datareader.models

import adminApi.com.datareader.models.abstractions.DataModelClass
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.ProducerData
import adminApi.com.general.models.data.ProductData

class ProductDataModel(val converter  : JsonPropertyConverter) : DataModelClass() {
    override var providerId: String = converter.strProp("productId")
    var producerId : String = converter.strProp("producerId")
    var producerCode : String = converter.strProp("producerCode")
    var categoriesIds : String = converter.strProp("categoriesIds",true)
    var name: String = converter.strProp("productName")
    var quantity: Int = converter.intProp("quantity")
    var price: Double = converter.doubleProp("price")
    var currency: String = converter.strProp("currency")
    var ean: String = converter.strProp("ean")
    var sizeX: Int = converter.intProp("sizeX")
    var sizeY: Int = converter.intProp("sizeY")
    var sizeZ: Int = converter.intProp("sizeZ")
    var weight : Int = converter.intProp("weight")
    var additionalParameters : String = converter.strProp("additionalParameters",true)

    override fun toData():ProductData{
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

}