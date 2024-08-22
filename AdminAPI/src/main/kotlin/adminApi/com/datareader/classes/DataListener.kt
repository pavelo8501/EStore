package adminApi.com.datareader.classes

import adminApi.com.datareader.data.ProviderManager
import adminApi.com.general.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class ProviderDataListener( val providerManager : ProviderManager) : CoroutineScope {

    override val coroutineContext: CoroutineContext = Job()
    override fun toString(): String =
        "CoroutineScope(coroutineContext=$coroutineContext)"

    fun onStart() {
        launch(Dispatchers.Default) {
            DataManager.producerSubject.collect { message ->
                if(message!=null){
                    val provider =  providerManager.getProvider(message.supplierId)
                    if (provider!=null){
                        provider.producers.addProducers(message.items)
                    }else{
                        throw Exception("Provider with given id ${message.supplierId} not found")
                    }
                }
            }
        }
        launch(Dispatchers.Default) {
            DataManager.categorySubject.collect { message ->
                if(message!=null){
                    val provider =  providerManager.getProvider(message.supplierId)
                    if (provider!=null){
                        provider.categories.addCategories(message.items)
                    }else{
                        throw Exception("Provider with given id ${message.supplierId} not found")
                    }
                }
            }
        }
        launch(Dispatchers.Default) {
            DataManager.productSubject.collect { message ->
                if(message!=null){
                    val provider =  providerManager.getProvider(message.supplierId)
                    if (provider!=null){
                        provider.products.addProducts(message.items)
                    }else{
                        throw Exception("Provider with given id ${message.supplierId} not found")
                    }
                }
            }
        }
    }
}