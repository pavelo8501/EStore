package adminApi.com.scheduler.models

import adminApi.com.common.dataflow.DataDispatcherMarker
import adminApi.com.database.services.SchedulerTaskEntity
import adminApi.com.general.classes.ExecutionResults
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.datetime.*

data class SchedulerTask (
    val entity: SchedulerTaskEntity
){
    val id: Int = entity.id.value
    val supplierId = entity.supplierId
    val task = entity.task
    val frequency = entity.frequency
    var lastUpdateAt :LocalDateTime?
        get() = entity.lastUpdateAt
        set(value) { entity.lastUpdateAt = value }

    var onUpdate: ((Int,String) -> Unit)? = null

    var launchFn: (() -> SharedFlow<ExecutionResults>)? = null

    fun LocalDateTime.Companion.now(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.UTC)
    }
    fun LocalDate.Companion.now(): LocalDate {
        return LocalDateTime.now().date
    }
    fun LocalTime.Companion.now(): LocalTime {
        return LocalDateTime.now().time
    }

    fun LocalDateTime.plus(value: Long, unit: DateTimeUnit.TimeBased): LocalDateTime {
        val timeZone = TimeZone.currentSystemDefault()
        return this.toInstant(timeZone)
            .plus(value, unit)
            .toLocalDateTime(timeZone)
    }

    fun check(){
//        if(lastUpdate == null){
//            this.onUpdate?.invoke(supplierId, containerName)
//        }else{
//            val nextUpdateAt = lastUpdate!!.plus(updateFrequency.toLong(), DateTimeUnit.MINUTE)
//            if(nextUpdateAt > LocalDateTime.now()){
//                this.onUpdate?.invoke(supplierId,containerName)
//            }
//        }
    }
}