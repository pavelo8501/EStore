package adminApi.com.datareader.classes

import adminApi.com.common.statistics.*
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
    override val dataContainer = DataFlowContainerImpl()


    fun setResult(dataList: List<JsonElement>, dataSetType:DataSetType) {
        dataContainer.dataList = dataList
        dataContainer.dataType = DataType.JSON
        dataContainer.dataSetType = dataSetType
        this.success = true
        this.count = dataList.size
    }

    fun setError(errCode: Int, errMsg: String) {
        this.success = false
        this.errorCode = errCode
        this.errorMessage = errMsg
    }
}