package adminApi.com.general

import adminApi.com.database.DBManager
import adminApi.com.database.services.SupplierEntity
import adminApi.com.general.classes.DataManagerListener
import adminApi.com.general.containers.CategoryContainer
import adminApi.com.general.containers.ProducersContainer
import adminApi.com.general.containers.ProductContainer
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ProducerData
import adminApi.com.general.models.data.ProductData
import adminApi.com.general.models.data.SupplierData
import kotlinx.coroutines.flow.MutableStateFlow


data class DataManagerMessage<T>(
    val supplierId:Int,
    val dataType:String,
    val items:List<T>
)

class DataManager(val db : DBManager) {
    companion object DataManagerConnector{
        fun sendProducers(id : Int, items :List<ProducerData>){
            producersSubject.value =  DataManagerMessage(id,"producers", items)
        }
//        fun sendCategories(id : Int, items :List<CategoryData>){
//            categoriesSubject.value =  DataManagerMessage(id,"categories", items)
//        }
//        fun sendProducts(id : Int, items :List<ProductData>) {
//            productsSubject.value = DataManagerMessage(id,"products", items)
//        }
        val producersSubject = MutableStateFlow<DataManagerMessage<ProducerData>?>(null)
//        val categoriesSubject = MutableStateFlow<DataManagerMessage<CategoryData>?>(null)
//        val productsSubject = MutableStateFlow<DataManagerMessage<ProductData>?>(null)
    }

    private val listener = DataManagerListener(this)

    private val supplierList = mutableListOf<SupplierData>()
    fun getSuppliers():List<SupplierData>{
        return supplierList.toList()
    }
    fun loadSuppliers(callback: ((List<SupplierData>) -> Unit?)?){
        val result =  db.suppliersService().select<SupplierEntity>()
        for (entity in result) {
            try {
                this.supplierList.add(entity.toModel())
            }catch (e:Exception){
                println(e.message)
            }
        }
        callback?.invoke(getSuppliers())
    }

//    var onDataManagerReady : ((DataManager)->Unit)? = null
//    private fun notifyReady(){
//        onDataManagerReady?.invoke(this)
//    }

    init {
        this.listener.onStart()
    }

    val producerContainer = ProducersContainer(db)
    val categoryContainer = CategoryContainer(db)
    val productContainer = ProductContainer(db)

    fun init() {
        producerContainer.onSendDataToReader = {
                data, containerName ->  sendProducers(1,data)
        }
        producerContainer.init()
        categoryContainer.init()
        productContainer.init()
    }

}