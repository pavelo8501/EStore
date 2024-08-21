package adminApi.com.datareader.data

import adminApi.com.datareader.classes.ProviderDataListener
import adminApi.com.datareader.connectors.ActionConnector
import adminApi.com.datareader.connectors.Connector
import adminApi.com.general.DataManagerMessage
import adminApi.com.general.models.data.CategoryData
import adminApi.com.general.models.data.ProducerData
import adminApi.com.general.models.data.ProductData
import adminApi.com.general.models.data.SupplierData
import kotlinx.coroutines.flow.MutableStateFlow
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
            producersSubject.value =  DataManagerMessage(id,"producers", items)
        }
        fun sendCategories(id : Int, items :List<CategoryData>){
            categoriesSubject.value =  DataManagerMessage(id,"categories", items)
        }
        fun sendProducts(id : Int, items :List<ProductData>) {
            productsSubject.value = DataManagerMessage(id,"products", items)
        }
        val producersSubject = MutableStateFlow<DataManagerMessage<ProducerData>?>(null)
        val categoriesSubject = MutableStateFlow<DataManagerMessage<CategoryData>?>(null)
        val productsSubject = MutableStateFlow<DataManagerMessage<ProductData>?>(null)
    }

    private val dataProviders : MutableMap<String,DataProvider> = mutableMapOf()
//    private val producersSubject = CompletableFuture<ProviderUpdateArgs>()
//    fun subscribeToProducersChange():CompletableFuture<ProviderUpdateArgs>{
//        return producersSubject
//    }

    private val dataListener = ProviderDataListener(this)

    init {
        this.dataListener.onStart()
    }

    private fun listenProviderForUpdates(provider: DataProvider){
//        provider.onUpdated = { params -> run {
//              val subjectValue = ProviderUpdateArgs(provider.providerId,params.containerName,(params.dataContainer as DataContainer<IDataModel>))
//            }
//        }
    }

    private fun <T:Connector>makeConnector(name:String, clazz:KClass<T>):T{
        val connector = clazz.createInstance()
        if(connector != null) {
          //  connector.setSettings(settings)
            return connector
        }
        throw Exception("Connector $name not found")
    }
    fun initProvidersFromSupplierList(suppliers:List<SupplierData>){
        for(supplier in suppliers){
            when(supplier.name){
                "action" -> createDataProvider<ActionConnector>(supplier.supplierId,supplier.name)
            }
        }
    }
    fun <T:Connector>createDataProvider(id:Int,name:String):DataProvider?{
        if(!dataProviders.any { it.key == name }){
            val connector =  makeConnector(name,ActionConnector::class)
            val newProvider =  DataProvider(id, name)
            newProvider.setConnector(connector)
            listenProviderForUpdates(newProvider)
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
    fun getProvider(id:Int):DataProvider?{
       return this.dataProviders.entries.firstOrNull{it.value.supplierId == id}?.value
    }


    fun getCategories(providerName: String) {
        getDataProvider(providerName)?.getCategories()
    }

    fun getProducers(providerName: String? = null) {
        if (providerName == null) {
            for (provider in dataProviders.values) {
                provider.getProducers()
            }
        }else{
            getDataProvider(providerName)?.getProducers()
        }
    }

    fun getProducts(providerName: String? = null) {
        if (providerName == null) {
            for (provider in dataProviders.values) {
                provider.getProducts()
            }
        }else{
            getDataProvider(providerName)?.getProducts()
        }
    }

//    private fun getCategories(service : RestDataService) {
//         val method = service.connector.getMethod("getCategories")
//         runBlocking {
//           val result =  service.executeMethod(method)
//             if(result!=null){
//                 if(dataProviders.containsKey("action")){
//                     dataProviders["action"]!!.categories.setSourceData(result.data)
//                 }
//             }
//         }
//    }



}