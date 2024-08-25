package adminApi.com.general.classes

import adminApi.com.datareader.data.ProviderManager
import adminApi.com.general.DataListType
import adminApi.com.general.DataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DataManagerListener(val dataManager : DataManager) : CoroutineScope {

    override val coroutineContext: CoroutineContext = Job()
    override fun toString(): String =
        "CoroutineScope(coroutineContext=$coroutineContext)"

    fun onStart() {
        launch(Dispatchers.Default) {
            ProviderManager.producerSubject.collect { message ->
                if (message != null) {
                    when(message.listType){
                        DataListType.UPDATE -> {
                            dataManager.producerContainer.processDataFromDataProvider(message.supplierId, message.items)
                        }
                        DataListType.REMOVE -> {
                            dataManager.producerContainer.processRemovalListFromDataProvider(message.supplierId, message.items)
                        }
                    }
                }
            }
        }
        launch(Dispatchers.Default){
            ProviderManager.categorySubject.collect{ message ->
                if (message != null) {
                    when(message.listType){
                        DataListType.UPDATE -> {
                            dataManager.categoryContainer.processDataFromDataProvider(message.supplierId, message.items)
                        }
                        DataListType.REMOVE -> {
                            dataManager.categoryContainer.processRemovalListFromDataProvider(message.supplierId, message.items)
                        }
                    }
                }
            }
        }
        launch(Dispatchers.Default){
            ProviderManager.productSubject.collect{ message ->
                if (message != null) {
                    when(message.listType){
                        DataListType.UPDATE -> {
                            dataManager.productContainer.processDataFromDataProvider(message.supplierId, message.items)
                        }
                        DataListType.REMOVE -> {
                            dataManager.productContainer.processRemovalListFromDataProvider(message.supplierId, message.items)
                        }
                    }
                }
            }
        }
    }
}