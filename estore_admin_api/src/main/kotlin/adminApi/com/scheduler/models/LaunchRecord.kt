package adminApi.com.scheduler.models

import adminApi.com.common.statistics.DataFlowContainer
import adminApi.com.general.classes.ExecutionResults
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

data class LaunchRecord(
    val supplierId: Int,
    val containerName: String,
    val fn : (Int, DataFlowContainer)-> SharedFlow<DataFlowContainer>
)