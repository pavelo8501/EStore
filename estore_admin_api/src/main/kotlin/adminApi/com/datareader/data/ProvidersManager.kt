package adminApi.com.datareader.data

import adminApi.com.common.dataflow.DataDispatcherMarker
import adminApi.com.datareader.classes.ProviderDataListener
import adminApi.com.datareader.connectors.ActionConnector
import adminApi.com.datareader.connectors.Connector
import adminApi.com.general.DataListType
import adminApi.com.general.DataManagerMessage
import adminApi.com.general.classes.ExecutionResults
import adminApi.com.general.models.SupplierElement
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ProducerData
import adminApi.com.general.models.data.ProductData
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

object DataHelper{

    private fun getChildCategories(parentCategory: CategoryData, data : List<CategoryData> ){
        val children = data.filter { it.parentCategoryId == parentCategory.providerId.toInt() }
        if(children.isNotEmpty()){
            for(child in children){
              //  parentCategory.children[child.categoryId]  = child
            }
            for (child in children){
                getChildCategories(child,data)
            }
        }
    }

    fun plainRecordsToNested(data: List<CategoryData>):MutableMap<Int,CategoryData>{
        val result :MutableMap<Int,CategoryData> = mutableMapOf()
        val root : List<CategoryData> = data.filter { it.parentCategoryId == 0 }
        for(rootCategory in root){
            getChildCategories(rootCategory,data)
            result[rootCategory.providerId.toInt()] = rootCategory
        }
        return result
    }
}


//data class ProviderUpdateArgs(
//    val providerId:Int,
//    val containerName : String,
//    val dataContainer : DataContainer<IDataModel>
//)

class ProviderManager() {

    companion object ProviderManagerConnector{

        fun sendProducers(id : Int, items :List<ProducerData>){
            producerSubject.value =  DataManagerMessage(id,"producers", items, DataListType.UPDATE)
        }
        fun sendProducersForRemoval(id : Int, items :List<ProducerData>){
            producerSubject.value =  DataManagerMessage(id,"producers", items, DataListType.REMOVE)
        }
        fun sendCategories(id : Int, items :List<CategoryData>){
            categorySubject.value =  DataManagerMessage(id,"categories", items, DataListType.UPDATE)
        }
        fun sendCategoriesForRemoval(id : Int, items :List<CategoryData>){
            categorySubject.value =  DataManagerMessage(id,"categories", items, DataListType.REMOVE)
        }

        fun sendProducts(id : Int, items :List<ProductData>) {
            productSubject.value = DataManagerMessage(id,"products", items, DataListType.UPDATE)
        }

        fun sendProductsForRemoval(id : Int, items :List<ProductData>){
            productSubject.value =  DataManagerMessage(id,"producers", items, DataListType.REMOVE)
        }
        val producerSubject = MutableStateFlow<DataManagerMessage<ProducerData>?>(null)
        val categorySubject = MutableStateFlow<DataManagerMessage<CategoryData>?>(null)
        val productSubject = MutableStateFlow<DataManagerMessage<ProductData>?>(null)
    }

   // private val coroutineContext = Job()
    val providerManagerScope = CoroutineScope(Job() + Dispatchers.IO + CoroutineName("Provider Manager Coroutine"))

    private val dataProviders : MutableMap<String,DataProvider> = mutableMapOf()

    private val dataListener = ProviderDataListener(this,providerManagerScope)

    init {
        this.dataListener.onStart()
    }

    private fun <T:Connector>makeConnector(name:String, clazz:KClass<T>):T{
        val connector = clazz.createInstance()
          //  connector.setSettings(settings)
        return connector
    }
    fun initProvidersFromSupplierList(suppliers:List<SupplierElement>){
        for(supplier in suppliers){
            when(supplier.name){
                "action" -> createDataProvider<ActionConnector>(supplier.supplierId,supplier.name, supplier.settings)
            }
        }
    }
    fun <T:Connector>createDataProvider(id:Int,name:String, authSettings : HashMap<String,String>? = null ):DataProvider?{
        if(!dataProviders.any { it.key == name }){
            val connector =  makeConnector(name,ActionConnector::class)
            if(authSettings!=null){
                connector.setAuthentication(authSettings)
            }
            val newProvider =  DataProvider(id, name)
            newProvider.setConnector(connector)
            this.dataProviders[name] = newProvider
            return newProvider
        }
        return null
    }
    fun getDataProvider(name:String):DataProvider?{
        if(dataProviders.containsKey(name)){
            return dataProviders[name]
        }
        return null
    }
    fun getDataProvider(id:Int):DataProvider?{
       return this.dataProviders.entries.firstOrNull{it.value.supplierId == id}?.value
    }

    private var categoriesSubject =  MutableSharedFlow<ExecutionResults>()
    fun updateCategories(providerId: Int, marker : DataDispatcherMarker? = null): SharedFlow<ExecutionResults>{
        getDataProvider(providerId)?.let {
            providerManagerScope.launch {
                it.updateCategories(marker?:DataDispatcherMarker("Temp","Container",0))
                categoriesSubject.emit(ExecutionResults(it.supplierId,"products"))
            }
        }
        return categoriesSubject
    }

    private var producersSubject =  MutableSharedFlow<ExecutionResults>()
    fun updateProducers(supplierId: Int, marker : DataDispatcherMarker? = null): SharedFlow<ExecutionResults> {
        getDataProvider(supplierId)?.let {
            providerManagerScope.launch {
                it.updateProducers(marker?:DataDispatcherMarker("Temp","Container",0))
                producersSubject.emit(ExecutionResults(it.supplierId,"producers"))
            }
        }
        return producersSubject
    }

    private var productsSubject =  MutableSharedFlow<ExecutionResults>()
    fun updateProducts(supplierId: Int, marker : DataDispatcherMarker? = null) : SharedFlow<ExecutionResults> {
        getDataProvider(supplierId)?.let {
            providerManagerScope.launch {
                it.updateProducts(marker?:DataDispatcherMarker("Temp","Container",0))
                productsSubject.emit(ExecutionResults(it.supplierId,"products"))
            }
        }
        return productsSubject
    }

}