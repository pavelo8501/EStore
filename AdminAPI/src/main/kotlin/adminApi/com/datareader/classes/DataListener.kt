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
            DataManager.producersSubject.collect { message ->
                if(message!=null){
                    val provider =  providerManager.getProvider(message.supplierId)
                    if (provider!=null){
                        message.items.forEach{ provider.producers.addProducer(it)}
                    }else{
                        throw Exception("Provider with given id ${message.supplierId} not found")
                    }
                }
            }
        }
    }
}