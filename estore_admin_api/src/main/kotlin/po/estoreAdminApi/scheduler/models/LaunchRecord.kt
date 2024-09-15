package po.estoreAdminApi.scheduler.models


import kotlinx.coroutines.flow.SharedFlow
import po.estoreAdminApi.common.statistics.DataFlowContainer

data class LaunchRecord(
    val supplierId: Int,
    val containerName: String,
    val fn : (Int, DataFlowContainer)-> SharedFlow<DataFlowContainer>
)