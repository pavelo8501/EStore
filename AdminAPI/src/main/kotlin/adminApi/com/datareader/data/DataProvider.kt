package adminApi.com.datareader.data

import adminApi.com.common.dataflow.DataDispatcherImpl
import adminApi.com.common.dataflow.DataFlowMeter
import adminApi.com.common.dataflow.DataFlowMeterCompanion
import adminApi.com.common.dataflow.MeasurableCallResultImpl
import adminApi.com.common.statistics.*
import adminApi.com.datareader.connectors.ActionConnector
import adminApi.com.datareader.connectors.Connector
import adminApi.com.datareader.data.containers.CategoryContainer
import adminApi.com.datareader.data.containers.DataContainer
import adminApi.com.datareader.data.containers.ProducerContainer
import adminApi.com.datareader.data.containers.ProductContainer
import adminApi.com.datareader.services.JsonFileDataService

data class ProviderUpdateParams(val providerId:Int, val containerName : String, val dataContainer : DataContainer<*>)

class DataProvider(val supplierId: Int, val name: String) : DataFlowMeter {

    companion object MeterCompanion : DataFlowMeterCompanion{
        override var parent: DataProvider? = null
        override var objectName: String = ""
        override var module: ModuleName = ModuleName.DATAREADER
        override var supplierName: String = ""
        override val measurements: MutableList<IMeasurableCallResult> = mutableListOf()

        override val onDispatcherResultsSubmitted = { result: ServiceCallResult -> this.registerMeasurement(result) }
        override val onMeterResultsSubmitted: ((MeasurableCallResult) -> Unit)? =
            { result: MeasurableCallResult -> this.submitResultForMeasurement(result.operation, result.elapsedTime) }

        override fun registerMeasurement(serviceCallResult: ServiceCallResult) {
            if (serviceCallResult.success) {
                val newRecord = MeasurableCallResultImpl(module, parent!!.name)
                newRecord.count = serviceCallResult.count
                this.measurements.add(newRecord)
            }
        }

        override fun submitResultForMeasurement(operation: String, elapsedTime: Long) {
            this.measurements.firstOrNull { it.operation == operation }?.elapsedTime = elapsedTime
        }
    }

    var onDataUpdated: ((name: String, data: List<Any>) -> Unit)? = null
    var onUpdated: ((data: ProviderUpdateParams) -> Unit)? = null

    val producers: ProducerContainer = ProducerContainer(ActionConnector.producerMappings(), supplierId)
    val categories: CategoryContainer = CategoryContainer(ActionConnector.categoryMappings(), supplierId)
    val products: ProductContainer = ProductContainer(ActionConnector.categoryMappings(), supplierId)

    var dataService = JsonFileDataService(ActionConnector())
    val dataDispatcher: DataDispatcherImpl = DataDispatcherImpl()

    var companion: DataFlowMeterCompanion = DataProvider.MeterCompanion
    override fun initCompanion() {
        this.companion = MeterCompanion
        companion.init("DataProvider", ModuleName.DATAREADER, name, this)
        dataDispatcher.init(DataProvider.MeterCompanion.onDispatcherResultsSubmitted)
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

    suspend fun getCategories(caller: String) = dataDispatcher.getData(DataCategory.CATEGORY) {
        startMeasure<DataDispatcherImpl>("getCategories", FlowType.RECEIVE, onMeterResultsSubmitted) {
            val dataReceived = dataService.executeCall("getCategories")
            getServiceTransferResult(dataReceived)
        }
        val dataReceived = dataService.executeCall("getCategories")
        categories.setDataFromSource(dataReceived.dataList, true)
    }

    suspend fun getProducers(caller: String) = dataDispatcher.getData(DataCategory.PRODUCER) {
        startMeasure<DataDispatcherImpl>("getProducers", FlowType.RECEIVE, onMeterResultsSubmitted) {
            val dataReceived = dataService.executeCall("getProducers")
            getServiceTransferResult(dataReceived)
        }
        val dataReceived = dataService.executeCall("getProducers")
        producers.setDataFromSource(dataReceived.dataList, true)
    }

    suspend fun getProducts(caller: String) = dataDispatcher.getData(DataCategory.PRODUCT) {
        startMeasure<DataDispatcherImpl>("getProducts", FlowType.RECEIVE, onMeterResultsSubmitted) {
            val dataReceived = dataService.executeCall("getProducts")
            getServiceTransferResult(dataReceived)
        }
        val dataReceived = dataService.executeCall("getProducts")
        products.setDataFromSource(dataReceived.dataList, true)
    }
}