package po.estoreAdminApi.datareader.data.containers


import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import po.estoreAdminApi.common.statistics.DataCategory
import po.estoreAdminApi.datareader.models.ProducerDataModel
import po.estoreAdminApi.general.models.data.ICommonData


class ProducerContainer(mappings: HashMap<String,String>, override var supplierId :Int ) : DataContainer<ProducerDataModel>() {

   override var dataCategory = DataCategory.PRODUCER

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