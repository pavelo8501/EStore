package po.estoreAdminApi.datareader.models


import kotlinx.serialization.json.JsonElement
import po.estoreAdminApi.datareader.models.abstractions.DataModelClass
import po.estoreAdminApi.general.models.data.ICommonData
import po.estoreAdminApi.general.models.data.ProducerData

class ProducerDataModel(jsonElement: JsonElement? = null) : DataModelClass(jsonElement) {

    override var providerId: String = strProp("producerId")
    var name: String = strProp("name")

    override fun toData(): ProducerData{
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