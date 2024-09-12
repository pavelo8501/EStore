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
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
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

class DataManager() {
    companion object DataManagerConnector{

        val producerSubject = MutableSharedFlow<DataManagerMessage<ProducerData>?>()
        suspend fun sendProducerUpdate(id : Int, items :List<ProducerData>){
            producerSubject.emit(DataManagerMessage(id,"producers", items, DataListType.UPDATE))
        }

        val categorySubject = MutableSharedFlow<DataManagerMessage<CategoryData>?>()
        suspend fun sendCategoryUpdate(id : Int, items :List<CategoryData>){
            categorySubject.emit(DataManagerMessage(id,"categories", items, DataListType.UPDATE))
        }

        val productSubject = MutableSharedFlow<DataManagerMessage<ProductData>?>()
        suspend fun sendProductUpdate(id : Int, items :List<ProductData>){
            productSubject.emit(DataManagerMessage(id,"categories", items, DataListType.UPDATE))
        }
    }

    var db : DBManager? = null

    val dataManagerScope = CoroutineScope(Job() + Dispatchers.Default + CoroutineName("Data Manager Coroutine"))

    private val listener = DataManagerListener(this)

    private val supplierList = mutableListOf<SupplierElement>()

    var onDataLoaded : ( () -> Unit) ? = null

    fun getSuppliers():List<SupplierElement>{
        return supplierList.toList()
    }
    fun loadSuppliers(callback: ((List<SupplierElement>) -> Unit?)? = null){
        if(db == null){
            return
        }

        if(this.supplierList.isNotEmpty() && callback != null){
            callback.invoke(getSuppliers())
        }else{
            db!!.suppliersService().select<SupplierEntity>().forEach {
                try {
                    val  supplier =  SupplierElement(it)
                    this.supplierList.add(supplier)
                }catch (e:Exception){
                    println(e.message)
                }
            }
            callback?.invoke(getSuppliers())
        }
    }


    var producerContainer : ProducersContainer? = null
    var categoryContainer : CategoryContainer? = null
    var productContainer : ProductContainer? = null

    fun init(db : DBManager) {

        this.db = db
        loadSuppliers()
        this.listener.onStart()

        producerContainer = ProducersContainer(getSuppliers(), db)
        producerContainer!!.onSendDataToReader = { update ->
            dataManagerScope.launch {
                sendProducerUpdate(update.supplierId, update.records)
            }
        }

        categoryContainer = CategoryContainer(getSuppliers(),db)
        categoryContainer!!.onSendDataToReader = { update ->
            dataManagerScope.launch {
                sendCategoryUpdate(update.supplierId,update.records)
            }
        }
        productContainer = ProductContainer(getSuppliers(),db)
        productContainer!!.onSendDataToReader = { update ->
            dataManagerScope.launch {
                sendProductUpdate(update.supplierId, update.records)
            }
        }

        runBlocking {
            producerContainer!!.init()
            categoryContainer!!.init()
            productContainer!!.init()
        }
        onDataLoaded?.invoke()
    }

}