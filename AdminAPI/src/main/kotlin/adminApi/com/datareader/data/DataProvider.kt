package adminApi.com.datareader.data

import adminApi.com.datareader.connectors.ActionConnector
import adminApi.com.datareader.connectors.Connector
import adminApi.com.datareader.data.containers.CategoryContainer
import adminApi.com.datareader.data.containers.DataContainer
import adminApi.com.datareader.data.containers.ProducerContainer
import adminApi.com.datareader.data.containers.ProductContainer
import adminApi.com.datareader.models.ProducerDataModel
import adminApi.com.datareader.services.DataService
import adminApi.com.datareader.services.JsonFileDataService
import adminApi.com.datareader.services.RestDataService
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ProducerData
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

//class  UpdatedParams<T>(name:String, records:List<T>, provider:DataProvider)

data class ProviderUpdateParams(val providerId:Int, val containerName : String, val dataContainer : DataContainer<*>)

class  DataProvider(val supplierId : Int, val name :String) {

    var onDataUpdated: ((name :String, data : List<Any>) -> Unit)?  = null
    var onUpdated: ((data:ProviderUpdateParams)->Unit)? = null

    val producers : ProducerContainer = ProducerContainer(ActionConnector.producerMappings(),supplierId )
    val categories : CategoryContainer = CategoryContainer(ActionConnector.categoryMappings(), supplierId )
    val products : ProductContainer = ProductContainer(ActionConnector.categoryMappings(), supplierId)

    var dataService : DataService? = null

    init {
        producers.onDataUpdated = { list -> ProviderManager.sendProducers(supplierId, list.map { it.toData() } ) }
    }

    fun <T:Connector>setConnector(connector:T){
        dataService = JsonFileDataService<T>(connector)
       // dataService = RestDataService<T>(connector)
    }

    fun getCategories(){
        runBlocking {
            val result =  dataService?.executeMethod("getCategories")
            if(result!=null){
                categories.setData(result.data)
            }
        }
    }

    fun getProducers() {
        runBlocking {
            val result =  dataService?.executeMethod("getProducers")
            if(result!=null){
                producers.setData(result.data)
            }
        }
    }

    fun getProducts() {
        runBlocking {
            val result =  dataService?.executeMethod("getProducts")
            if(result!=null){
                products.setData(result.data)
            }
        }
    }
}