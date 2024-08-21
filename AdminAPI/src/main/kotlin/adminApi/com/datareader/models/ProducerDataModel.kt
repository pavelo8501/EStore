package adminApi.com.datareader.models

import adminApi.com.datareader.models.abstractions.DataModelClass
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.ProducerData

class ProducerDataModel(val converter  : JsonPropertyConverter) : DataModelClass() {
    override var providerId: String = converter.strProp("producerId")
    var name: String = converter.strProp("name")

    override fun toData():ProducerData{
        return  ProducerData(this.id, this.supplierId, this.providerId,this.name)
    }

}