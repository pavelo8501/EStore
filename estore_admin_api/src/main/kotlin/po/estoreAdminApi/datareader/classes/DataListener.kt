package po.estoreAdminApi.datareader.classes

import kotlinx.coroutines.*
import po.estoreAdminApi.datareader.data.ProviderManager
import po.estoreAdminApi.general.DataManager
import kotlin.coroutines.CoroutineContext

class ProviderDataListener( val providerManager : ProviderManager, val listenerScope : CoroutineScope )  {

    fun onStart() {
        listenerScope.launch(Dispatchers.Default) {
            DataManager.producerSubject.collect { message ->
                if(message!=null){
                    val provider =  providerManager.getDataProvider(message.supplierId)
                    if (provider!=null){
                        provider.producers.supplyData(message.items)
                    }else{
                        throw Exception("Provider with given id ${message.supplierId} not found")
                    }
                }
            }
        }
        listenerScope.launch(Dispatchers.Default) {
            DataManager.categorySubject.collect { message ->
                if(message!=null){
                    val provider =  providerManager.getDataProvider(message.supplierId)
                    if (provider!=null){
                        provider.categories.supplyData(message.items)
                    }else{
                        throw Exception("Provider with given id ${message.supplierId} not found")
                    }
                }
            }
        }
        listenerScope.launch(Dispatchers.Default) {
            DataManager.productSubject.collect { message ->
                if (message != null) {
                    val provider = providerManager.getDataProvider(message.supplierId)
                    if (provider != null) {
                       val result = provider.products.supplyData(message.items)
                    } else {
                        throw Exception("Provider with given id ${message.supplierId} not found")
                    }
                }
            }
        }
    }
}