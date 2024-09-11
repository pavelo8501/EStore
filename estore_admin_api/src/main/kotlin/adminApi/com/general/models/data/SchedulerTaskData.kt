package adminApi.com.general.models.data

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class SchedulerTaskData (
    var id :Int,
    var supplierId :Int,
    var task : String,
    val frequency: Int,
    val lastUpdateAt: LocalDateTime
): ICommonData