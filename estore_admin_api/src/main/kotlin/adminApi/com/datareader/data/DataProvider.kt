package adminApi.com.datareader.data

import adminApi.com.common.dataflow.*
import adminApi.com.common.statistics.*
import adminApi.com.datareader.classes.DataServiceCallResult
import adminApi.com.datareader.connectors.ActionConnector
import adminApi.com.datareader.connectors.Connector
import adminApi.com.datareader.data.containers.CategoryContainer
import adminApi.com.datareader.data.containers.DataContainer
import adminApi.com.datareader.data.containers.ProducerContainer
import adminApi.com.datareader.data.containers.ProductContainer
import adminApi.com.datareader.services.DataService
import adminApi.com.datareader.services.JsonFileDataService

data class ProviderUpdateParams(val providerId:Int, val containerName : String, val dataContainer : DataContainer<*>)

class DataProvider(val supplierId: Int, val name: String) : DataFlowMeter {

    companion object MeterCompanion : DataFlowMeterCompanion{
        override var parent: Any? = null
        override var unitName: String = "DataProvider"
        override var module: ModuleName = ModuleName.DATAREADER
        override var supplierName: String = ""
        override val measurements: MutableList<Measurement> = mutableListOf()

        override val onDispatcherResultsSubmitted: ((FlowPoint) -> Unit) = {
             this.registerMeasurement(it)
        }
        override val onMeterResultsSubmitted: ((Measurement) -> Unit)? = {
            this.updateMeasurement(it)
        }



        override fun updateMeasurement(measurement: Measurement) {
            this.measurements.firstOrNull { it.methodName == measurement.methodName }?.also {
                it.elapsedTime = measurement.elapsedTime
                it.flowType = measurement.flowType
                it.success = measurement.success
            }
        }

        override fun registerMeasurement(data : FlowPoint) {
            val measurement = Measurement()
            measurement.also {
                it.module = data.module
                it.supplierName = data.supplierName?:""
                it.methodName = data.targetMethodName?: ""
            }
            this.measurements.add(measurement)
        }

        fun getLastMeasurement(): MeasurableCallResult? {
            val last = measurements.last()
            return last
        }
    }

    var onDataUpdated: ((name: String, data: List<Any>) -> Unit)? = null
    var onUpdated: ((data: ProviderUpdateParams) -> Unit)? = null

    val producers: ProducerContainer = ProducerContainer(ActionConnector.producerMappings(), supplierId)
    val categories: CategoryContainer = CategoryContainer(ActionConnector.categoryMappings(), supplierId)
    val products: ProductContainer = ProductContainer(ActionConnector.categoryMappings(), supplierId)

    var dataService = JsonFileDataService(ActionConnector())
    val dataDispatcher: DataDispatcher = DataDispatcher()

    var companion: MeterCompanion? = null
    override fun initCompanion() {
        companion = MeterCompanion
        companion!!.init("DataProvider", ModuleName.DATAREADER, name, this)
        dataDispatcher.subscribeToCallResults(onDispatcherResultsSubmitted)
    }

    init {

        initCompanion()
        producers.onDataForUpdated = { list -> ProviderManager.sendProducers(supplierId, list.map { it.toData() }) }
        producers.onDataForRemoval =
            { list -> ProviderManager.sendProducersForRemoval(supplierId, list.map { it.toData() }) }

        categories.onDataForUpdated = { list -> ProviderManager.sendCategories(supplierId, list.map { it.toData() }) }
        categories.onDataForRemoval =
            { list -> ProviderManager.sendCategoriesForRemoval(supplierId, list.map { it.toData() }) }

        products.onDataForUpdated = { list -> ProviderManager.sendProducts(supplierId, list.map { it.toData() }) }
        products.onDataForRemoval =
            { list -> ProviderManager.sendProductsForRemoval(supplierId, list.map { it.toData() }) }
    }

    fun <T : Connector> setConnector(connector: T) {
        dataService = JsonFileDataService(connector)
        // dataService = RestDataService<T>(connector)
    }

    suspend fun updateCategories(container: DataFlowContainer) = dataDispatcher.getData(
        container,
        RouteHop(ModuleName.DATAREADER, supplierName, DataCategory.CATEGORY,"getCategories")){
        val categoriesReceived =  startMeasureSuspended<DataServiceCallResult>(container.lastKnownTargetMethod(), FlowType.RECEIVE, onMeterResultsSubmitted) {
            dataService.executeCall(container.lastKnownTargetMethod())
        }
        setCallResult(categoriesReceived,getLastMeasurement())
    }

    suspend fun updateProducers(container: DataFlowContainer) = dataDispatcher.getData(
        container,
        RouteHop(ModuleName.DATAREADER, supplierName, DataCategory.PRODUCER,"getProducers"),
        producers::setDataFromSource){
            val producersReceived = startMeasureSuspended<DataServiceCallResult>(container.lastKnownTargetMethod(), FlowType.RECEIVE, onMeterResultsSubmitted) {
                dataService.executeCall(container.lastKnownTargetMethod())
            }
            setCallResult(producersReceived, getLastMeasurement())
    }

    suspend fun updateProducts(container: DataFlowContainer) = dataDispatcher.getData(
        container,
        RouteHop(ModuleName.DATAREADER, supplierName, DataCategory.PRODUCT,"getProducts"),
        products::setDataFromSource) {
       val productsReceived = startMeasureSuspended<DataServiceCallResult>(container.lastKnownTargetMethod(), FlowType.RECEIVE, onMeterResultsSubmitted) {
            dataService.executeCall(container.lastKnownTargetMethod())
        }
        setCallResult(productsReceived,getLastMeasurement())
    }
}