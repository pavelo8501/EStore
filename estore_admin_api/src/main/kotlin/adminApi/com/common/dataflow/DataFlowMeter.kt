package adminApi.com.common.dataflow

import adminApi.com.common.statistics.*
import adminApi.com.datareader.data.DataProvider
import kotlin.reflect.KClass

data class MeasurableCallResultImpl(

    override val module: ModuleName,
    override val supplierName: String,
) : IMeasurableCallResult {
    override val unitName: DataCategory = DataCategory.PRODUCER
    override val operation = ""
    override val flowType: FlowType = FlowType.RECEIVE
    override val success: Boolean = false
    override var count: Int = 0
    override var elapsedTime: Long = 0
}

interface DataFlowMeter {
    abstract fun initCompanion()
}

interface DataFlowMeterCompanion {
    var parent : Any?
    var objectName: String
    var module: ModuleName
    var supplierName: String
    val measurements: MutableList<IMeasurableCallResult>
    val onDispatcherResultsSubmitted: ((ServiceCallResult) -> Unit)?
    val onMeterResultsSubmitted: ((MeasurableCallResult) -> Unit)?

    fun init(name: String, module: ModuleName, supplierName: String, parent: Any) {
        this.objectName = name
        this.module = module
        this.supplierName = supplierName
        this.parent = parent
    }

    abstract fun registerMeasurement(serviceCallResult: ServiceCallResult)
    abstract fun registerMeasurement(operation: String, elapsedTime: Long)
}

interface DataFlowMeasurable {
    suspend fun <T> startMeasureSuspended(
        operationName: String,
        flowType: FlowType,
        onResult: ((MeasurableCallResult) -> Unit)?,
        block: suspend (T) -> Unit
    ): T {
        val measurement = MeasurableCallResult(operationName, flowType)
        measurement.start()
        block(this as T)
        measurement.stop()
        onResult?.invoke(measurement)
        return (this as T)
    }

    fun <T> startMeasure(
    operationName: String,
    flowType: FlowType,
    onResult: ((MeasurableCallResult) -> Unit)?,
    block:  T.() -> Unit
    ): T {
        val measurement = MeasurableCallResult(operationName, flowType)
        measurement.start()
        block(this as T)
       // (this as T).block()
        measurement.stop()
        onResult?.invoke(measurement)
        return (this as T)
    }
}


