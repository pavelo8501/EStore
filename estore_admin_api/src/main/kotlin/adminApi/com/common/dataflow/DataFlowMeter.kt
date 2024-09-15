package adminApi.com.common.dataflow

import adminApi.com.common.statistics.*
import adminApi.com.datareader.data.DataProvider
import kotlin.reflect.KClass

//data class MeasurableCallResultImpl(
//
//    override val module: ModuleName,
//    override val supplierName: String,
//) : MeasurableCallResult {
//    override val unitName: DataCategory = DataCategory.PRODUCER
//    override val operation = ""
//    override val flowType: FlowType = FlowType.RECEIVE
//    override val success: Boolean = false
//    override var count: Int = 0
//    override var elapsedTime: Long = 0
//}

interface DataFlowMeter {
    abstract fun initCompanion()
}

interface DataFlowMeterCompanion {
    var parent : Any?
    var unitName: String
    var module: ModuleName
    var supplierName: String
    val measurements: MutableList<Measurement>
    val onDispatcherResultsSubmitted: ((FlowPoint) -> Unit)?
    val onMeterResultsSubmitted: ((Measurement) -> Unit)?

    fun init(name: String, module: ModuleName, supplierName: String, parent: Any) {
        this.unitName = name
        this.module = module
        this.supplierName = supplierName
        this.parent = parent
    }

    fun registerMeasurement(data : FlowPoint)
    fun updateMeasurement(measurement: Measurement)
}

interface DataFlowMeasurable {
    suspend  fun <T> startMeasureSuspended(
        methodName: String,
        flowType: FlowType,
        onResult: ((Measurement) -> Unit)?,
        block: suspend () -> T
    ): T {
        val measurement = Measurement()
        measurement.also {
            it.flowType = flowType
            it.methodName = methodName
        }
        measurement.start()
        val result = block.invoke()
        measurement.stop()
        onResult?.invoke(measurement)
        return result
    }

    fun <T> startMeasure(
    methodName: String,
    flowType: FlowType,
    onResult: ((Measurement) -> Unit)?,
    block: () -> T
    ): T {
        val measurement = Measurement()
        measurement.also {
            it.flowType = flowType
            it.methodName = methodName
        }
        measurement.start()
        val result = block.invoke()
        measurement.stop()
        onResult?.invoke(measurement)
        return result
    }
}


