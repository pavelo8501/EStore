package adminApi.com.datareader.models

import adminApi.com.datareader.models.abstractions.DataModelClass
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProducerData
import kotlinx.serialization.json.JsonElement

class ProducerDataModel(jsonElement: JsonElement? = null) : DataModelClass(jsonElement) {

    override var providerId: String = strProp("producerId")
    var name: String = strProp("name")

    override fun toData():ProducerData{
        return  ProducerData(this.id, this.supplierId, this.providerId,this.name)
    }

    override fun fromData(data: ICommonData) {
        if (data is ProducerData) {
            this.id = data.id
            this.supplierId = data.supplierId
            this.providerId = data.providerId
            this.name = data.name
        }
        this.markedForRemoval = data.markedForRemoval
    }

    init {
        val a = 10
    }
}