package po.estoreAdminApi.datareader.classes

import kotlinx.serialization.json.JsonElement
import po.estoreAdminApi.common.statistics.DataFlowContainer
import po.estoreAdminApi.common.statistics.DataSetType
import po.estoreAdminApi.common.statistics.DataType
import po.estoreAdminApi.common.statistics.ServiceCallResult


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
    override val dataContainer = DataFlowContainer()


    fun setResult(dataList: List<JsonElement>, dataSetType: DataSetType) {
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