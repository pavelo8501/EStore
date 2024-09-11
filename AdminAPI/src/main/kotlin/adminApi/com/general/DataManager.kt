package adminApi.com.general

import adminApi.com.database.DBManager
import adminApi.com.database.services.SupplierEntity
import adminApi.com.general.classes.DataManagerListener
import adminApi.com.general.containers.CategoryContainer
import adminApi.com.general.containers.DataContainerUpdate
import adminApi.com.general.containers.ProducersContainer
import adminApi.com.general.containers.ProductContainer
import adminApi.com.general.models.SupplierElement
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ProducerData
import adminApi.com.general.models.data.ProductData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


enum class DataListType{
    UPDATE,
    REMOVE,
}

data class DataManagerMessage<T>(
    val supplierId:Int,
    val dataType:String,
    val items:List<T>,
    val listType:DataListType
)

class DataManager(val db : DBManager) {
    companion object DataManagerConnector{
        fun sendProducerUpdate(id : Int, items :List<ProducerData>){
            producerSubject.value =  DataManagerMessage(id,"producers", items, DataListType.UPDATE)
        }

        fun sendCategoryUpdate(id : Int, items :List<CategoryData>){
            categorySubject.value =  DataManagerMessage(id,"categories", items, DataListType.UPDATE)
        }

        fun sendProductUpdate(id : Int, items :List<ProductData>){
            productSubject.value =  DataManagerMessage(id,"categories", items, DataListType.UPDATE)
        }

        val producerSubject = MutableStateFlow<DataManagerMessage<ProducerData>?>(null)
        val categorySubject = MutableStateFlow<DataManagerMessage<CategoryData>?>(null)
        val productSubject = MutableStateFlow<DataManagerMessage<ProductData>?>(null)
    }

    val dataManagerScope = CoroutineScope(Dispatchers.Default)

    private val listener = DataManagerListener(this)

    private val supplierList = mutableListOf<SupplierElement>()

    var onDataLoaded : ( () -> Unit) ? = null

    fun getSuppliers():List<SupplierElement>{
        return supplierList.toList()
    }
    fun loadSuppliers(callback: ((List<SupplierElement>) -> Unit?)? = null){
        val result =  db.suppliersService().select<SupplierEntity>()
        for (entity in result) {
            try {
                val  supplier =  SupplierElement(entity)
                this.supplierList.add(supplier)
            }catch (e:Exception){
                println(e.message)
            }
        }
        callback?.invoke(getSuppliers())
    }

    init {
        loadSuppliers()
        this.listener.onStart()
    }

    val producerContainer = ProducersContainer(getSuppliers(), db)
    val categoryContainer = CategoryContainer(getSuppliers(),db)
    val productContainer = ProductContainer(getSuppliers(),db)

    fun init() {
        producerContainer.onSendDataToReader = { update ->  sendProducerUpdate(update.supplierId,update.records) }
        categoryContainer.onSendDataToReader = { update ->
            sendCategoryUpdate(update.supplierId,update.records)
        }
        productContainer.onSendDataToReader = { update ->  sendProductUpdate(update.supplierId, update.records)  }

        runBlocking {
            producerContainer.init()
            categoryContainer.init()
            productContainer.init()
        }
        onDataLoaded?.invoke()
    }

}