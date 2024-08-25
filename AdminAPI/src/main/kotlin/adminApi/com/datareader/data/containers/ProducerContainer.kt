package adminApi.com.datareader.data.containers

import adminApi.com.datareader.models.ProducerDataModel
import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProducerData
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement


class ProducerContainer(mappings: HashMap<String,String>, override var supplierId :Int ) : DataContainer<ProducerDataModel>() {

   override var containerType = ContainerType.PRODUCER

   override fun createDataItem(source:JsonElement, mapping: HashMap<String,String>? ) : ProducerDataModel {
        return ProducerDataModel(source).also {
            it.supplierId = supplierId
        }
    }
    override suspend fun supplyData(dataItems : List<ICommonData>):Boolean{
        val producers = dataItems.map { data ->  ProducerDataModel().also{ it.fromData(data) }}
        return setData(producers,true)
    }

}