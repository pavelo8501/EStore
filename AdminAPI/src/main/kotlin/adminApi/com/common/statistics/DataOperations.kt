package adminApi.com.common.statistics

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


enum class ModuleName{
    GENERAL,
    DATAREADER,
    DATABASE,
}

enum class FlowType{
    RECEIVE,
    SEND,
    UPDATE,
    DELETE
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

interface CallResult {
    val success: Boolean
    val count: Int
}

data class CallResultImpl(
    val dataList: List<*>
) : CallResult {
    override val success: Boolean = true
    override val count: Int
        get() = dataList.size
}

interface ServiceCallResult : CallResult {
    val dataType: DataType
    val dataList: List<*>
    val errorCode: Int?
    val errorMessage: String?
}

interface IMeasurableCallResult : CallResult {
    val module: ModuleName
    val supplierName: String
    val unitName: DataCategory
    val operation: String
    val flowType: FlowType
    var elapsedTime: Long
}


class MeasurableCallResult(
    override val operation: String,
    override val flowType: FlowType,

    ) : IMeasurableCallResult {

    override val success: Boolean = false
    override val module: ModuleName = ModuleName.DATAREADER
    override val supplierName: String = ""
    override val unitName: DataCategory = DataCategory.PRODUCER

    override var count: Int = 0
    override var elapsedTime: Long = 0

    private var startTime: Long = 0

    fun start() {
        this.startTime = System.currentTimeMillis()
    }

    fun stop() {
        this.elapsedTime = System.currentTimeMillis() - this.startTime
    }

    override fun toString(): String {
        return "Module: $module, Unit: $unitName, Operation: $operation, FlowType: $flowType, Count: $count, ElapsedTime: $elapsedTime"
    }

}