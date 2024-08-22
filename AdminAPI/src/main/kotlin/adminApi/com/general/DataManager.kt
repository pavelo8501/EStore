package adminApi.com.general

import adminApi.com.database.DBManager
import adminApi.com.database.services.SupplierEntity
import adminApi.com.general.classes.DataManagerListener
import adminApi.com.general.containers.CategoryContainer
import adminApi.com.general.containers.DataContainerUpdate
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
        fun sendProducerUpdate(id : Int, items :List<ProducerData>){
            producerSubject.value =  DataManagerMessage(id,"producers", items)
        }
        fun sendCategoryUpdate(id : Int, items :List<CategoryData>){
            categorySubject.value =  DataManagerMessage(id,"categories", items)
        }
        fun sendProductUpdate(id : Int, items :List<ProductData>){
            productSubject.value =  DataManagerMessage(id,"categories", items)
        }
        val producerSubject = MutableStateFlow<DataManagerMessage<ProducerData>?>(null)
        val categorySubject = MutableStateFlow<DataManagerMessage<CategoryData>?>(null)
        val productSubject = MutableStateFlow<DataManagerMessage<ProductData>?>(null)
    }

    private val listener = DataManagerListener(this)

    private val supplierList = mutableListOf<SupplierData>()

    var onDataLoaded : ( () -> Unit) ? = null

    fun getSuppliers():List<SupplierData>{
        return supplierList.toList()
    }
    fun loadSuppliers(callback: ((List<SupplierData>) -> Unit?)?){
        val result =  db.suppliersService().select<SupplierEntity>()
        for (entity in result) {
            try {
                val  supplier = entity.toModel()
                this.supplierList.add(supplier)
            }catch (e:Exception){
                println(e.message)
            }
        }
        callback?.invoke(getSuppliers())
    }

    init {
        this.listener.onStart()
    }

    val producerContainer = ProducersContainer(db)
    val categoryContainer = CategoryContainer(db)
    val productContainer = ProductContainer(db)

    fun init() {
        producerContainer.onSendDataToReader = { update ->  sendProducerUpdate(update.supplierId,update.records) }
        producerContainer.init()

        categoryContainer.onSendDataToReader = { update ->  sendCategoryUpdate(update.supplierId,update.records) }
        categoryContainer.init()

        productContainer.onSendDataToReader = { update ->  sendProductUpdate(update.supplierId, update.records)  }
        productContainer.init()

        onDataLoaded?.invoke()

    }

}