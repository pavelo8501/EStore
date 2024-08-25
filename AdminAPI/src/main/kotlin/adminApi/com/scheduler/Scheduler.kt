package adminApi.com.scheduler

import adminApi.com.database.DBManager
import adminApi.com.general.classes.ExecutionResults
import adminApi.com.scheduler.models.LaunchRecord
import adminApi.com.scheduler.models.ScheduleRecord
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class Scheduler(private val db:DBManager) {

    var active = false
    val schedule = mutableListOf<ScheduleRecord>()
    val schedulerScope = CoroutineScope(Job())
    val launchRecords = mutableListOf<LaunchRecord>()

    fun LocalDateTime.Companion.now(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.UTC)
    }

    private fun registerStart(record: ScheduleRecord){
        db.updateScheduleService().dbQuery {
            record.lastUpdate = LocalDateTime.now()
        }
    }
    private fun registerFinish(){

    }

    private suspend fun  startUpdate(record: ScheduleRecord) {

        if(record.launchFn != null){
            registerStart(record)
            record.launchFn!!.invoke().also {
                it.onCompletion {
                    val onCompletion = true
                }.collect{
                    registerFinish()
                }
            }
        }
    }

    private fun bindCallbacks(record: ScheduleRecord){
        val launchRecord = launchRecords.find { launchRecord -> launchRecord.supplierId == record.supplierId && launchRecord.containerName == record.containerName }
        if (launchRecord != null){
            record.launchFn = launchRecord.fn
        }

        record.onUpdate = { supplierId, containerName ->
            schedulerScope.launch {
                startUpdate(record)
            }
        }
    }

    init {
        val records  =  db.updateScheduleService().select().map { ScheduleRecord(it)   }
        records.forEach{
            bindCallbacks(it)
            schedule.add(it)
        }
    }

    fun registerLaunchFn(supplierId: Int, containerName:String, fn : ()-> SharedFlow<ExecutionResults>) {
       launchRecords.add(LaunchRecord(supplierId, containerName.lowercase(), fn))
       val scheduleRecord = schedule.find { it.supplierId == supplierId && it.containerName == containerName.lowercase() }
       if (scheduleRecord != null){
           scheduleRecord.launchFn = fn
       }
        fn().onCompletion {
            val a =  it
        }
    }

    fun start(){
        active = true
        schedulerScope.launch {
            while(active){
                schedule.forEach {
                    it.check()
                }
                delay(50000)
            }
        }
    }

    fun stop(){
        active = false
    }

}