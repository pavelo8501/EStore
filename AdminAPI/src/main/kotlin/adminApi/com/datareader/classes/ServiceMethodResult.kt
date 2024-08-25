package adminApi.com.datareader.classes

import adminApi.com.common.statistics.CallResultImpl
import adminApi.com.common.statistics.DataType
import adminApi.com.common.statistics.ServiceCallResult
import adminApi.com.datareader.models.ProducerDataModel
import kotlinx.serialization.json.JsonElement


class DataServiceCallResult(
    override val dataType: DataType,
    val connectorName: String,
    val methodName: String,
    val tableName: String
) : ServiceCallResult {
    override var success: Boolean = false
    override var errorCode: Int? = null
    override var errorMessage: String? = null
    override var count: Int = 0
    override var dataList: List<JsonElement> = emptyList()

    fun setResult(dataList: List<JsonElement>) {
        this.dataList = dataList
        this.success = true
        this.count = dataList.size
    }

    fun setError(errCode: Int, errMsg: String) {
        this.success = false
        this.errorCode = errCode
        this.errorMessage = errMsg
    }
}