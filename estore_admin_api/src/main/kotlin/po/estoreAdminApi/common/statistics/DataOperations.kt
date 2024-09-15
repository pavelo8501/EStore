package po.estoreAdminApi.common.statistics



enum class ModuleName{
    GENERAL,
    DATAREADER,
    DATABASE,
    SCHEDULER
}

enum class FlowType {
    RECEIVE,
    SEND,
    UPDATE,
    DELETE,
    BUFFERED,
}

enum class DataCategory {
    PRODUCER,
    CATEGORY,
    PRODUCT,
}

enum class DataType {
    JSON,
    ENTITY,
    DATARECORD,
    OBJECT
}

enum class DataSetType {
    COMPLETE,
    PARTIAL
}

interface CallResult {
    var success: Boolean
    var count: Int
}

data class CallResultData(
    val dataList: List<*>
) : CallResult {
    override var success: Boolean = true
    override var count: Int = 0
        get() = dataList.size
}

interface ServiceCallResult : CallResult {
    val dataType: DataType
    val errorCode: Int?
    val errorMessage: String?
    val dataContainer: FlowDataContext

    fun getCallResult(): CallResult{
        return this
    }
}

interface MeasurableCallResult : CallResult {
    val module: ModuleName
    val supplierName: String
    val dataCategory: DataCategory
    val methodName: String
    val flowType: FlowType
    var elapsedTime: Long

    fun getCallResult(): CallResult{
        return this
    }
}


class Measurement( ) : MeasurableCallResult {

    override var methodName: String = ""
    override var flowType: FlowType = FlowType.RECEIVE
    override var success: Boolean = false
    override var module: ModuleName = ModuleName.DATAREADER
    override var supplierName: String = ""
    override var dataCategory: DataCategory = DataCategory.PRODUCER

    override var count: Int = 0
    override var elapsedTime: Long = 0

    private var startTime: Long = 0

    fun importData(data: CallResult){
        this.count = data.count
        this.success = data.success
    }

    fun start() {
        this.startTime = System.currentTimeMillis()
    }

    fun stop() {
        this.elapsedTime = System.currentTimeMillis() - this.startTime
    }

    override fun toString(): String {
        return "Module: $module, DataCategory: $dataCategory, Operation: $methodName, FlowType: $flowType, Count: $count, ElapsedTime: $elapsedTime"
    }
}


interface FlowPoint {
    val module: ModuleName
    val supplierName: String?
    val dataCategory: DataCategory?
    val targetMethodName: String?
}

data class RouteHop(
    override val module: ModuleName,
    override val supplierName: String? =null,
    override val dataCategory: DataCategory? = null,
    override val targetMethodName: String?=null,
) : FlowPoint{
    val measurementLog: MutableList<MeasurableCallResult> = mutableListOf()
}

interface FlowDataContext {
    val dataList: List<*>
    val dataSetType: DataSetType
    val dataType: DataType
    val routes: List<RouteHop>
}

class DataFlowContainer(
) : FlowDataContext {
    override var dataList: List<*> = listOf<Any>()
    override var dataType: DataType = DataType.JSON
    override var dataSetType: DataSetType = DataSetType.COMPLETE
    override val routes: MutableList<RouteHop> = mutableListOf()

    fun init(container: FlowDataContext) {
        this.dataList = container.dataList
        this.dataSetType = container.dataSetType
        this.dataType = container.dataType
    }

    fun <T>updateData(dataList: List<T>, dataSetType: DataSetType, dataType: DataType) {
        this.dataList = dataList
        this.dataSetType = dataSetType
        this.dataType = dataType
        lastHop()?.measurementLog?.lastOrNull()?.also {
            it.count = dataList.size
            it.success = true
        }
    }

    fun importData(container: DataFlowContainer){
        this.dataList= container.dataList
        this.dataSetType = container.dataSetType
        this.dataType = container.dataType
        lastHop()?.measurementLog?.lastOrNull()?.also {
            it.count = dataList.size
            it.success = true
        }
    }


    fun addRouteInformation(route: RouteHop) {
        this.routes.add(route)

    }

    fun addMeasurement(measurement: MeasurableCallResult) {
        lastHop()?.measurementLog?.add(measurement)
    }

    fun lastHop(): RouteHop? {
        return routes.lastOrNull()
    }

    fun lastKnownTargetMethod(): String{
        val result = routes.lastOrNull()?.targetMethodName
        if(result == null){
            return "Unknown"
        }
        return result
    }

}