package adminApi.com.datareader.data.containers

import adminApi.com.datareader.models.ProducerDataModel
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.ProducerData
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement


class ProducerContainer(mappings: HashMap<String,String>, override var supplierId :Int ) : DataContainer<ProducerDataModel>() {

   override fun createDataItem(source:JsonElement, mapping: HashMap<String,String>? ) : ProducerDataModel {
        return ProducerDataModel(JsonPropertyConverter(source)).also {
            it.supplierId = supplierId
            it.converter.setProviderData(source,mapping)
        }
    }

    fun addProducer(dataItem : ProducerData){
        addData(dataItem)
    }

}