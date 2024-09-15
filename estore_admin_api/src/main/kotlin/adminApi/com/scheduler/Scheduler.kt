package adminApi.com.scheduler

import adminApi.com.common.dataflow.DataDispatcher
import adminApi.com.common.statistics.DataFlowContainer
import adminApi.com.common.statistics.ModuleName
import adminApi.com.database.DBManager
import adminApi.com.datareader.data.ProviderManager
import adminApi.com.general.classes.ExecutionResults
import adminApi.com.scheduler.models.LaunchRecord
import adminApi.com.scheduler.models.SchedulerTask
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime

class Scheduler(private val db:DBManager) {

    var active = false
    val launchRecords = mutableListOf<LaunchRecord>()
    private val tasks  : List<SchedulerTask> = db.schedulerTaskService().getTaskList()

    private val schedulerContext = Job() + Dispatchers.Default + CoroutineName("Scheduler Coroutine")
    private val schedulerScope = CoroutineScope(schedulerContext)

    fun LocalDateTime.Companion.now(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.UTC)
    }

    fun registerLaunchFn(supplierId: Int, taskName:String, fn : (Int, DataFlowContainer?)-> SharedFlow<DataFlowContainer>) {
       launchRecords.add(LaunchRecord(supplierId, taskName.lowercase(), fn))
    }

    suspend fun updateTask(containerName: String, supplierId: Int){

        when(containerName){
            "products" -> {
                launchRecords.find { it.containerName == "producers" }?.let {
                    it.fn.invoke(supplierId, DataDispatcher.createDispatchContainer(ModuleName.SCHEDULER,"updateProducers")).collect{
                        val result = it
                    }
                }

            }
        }
    }

    suspend fun launchTasks(){
        tasks.forEach {
            val nowTime = LocalDateTime.Companion.parse(Clock.System.now().toLocalDateTime(TimeZone.UTC).toString())
            if(it.lastUpdateAt == null || it.lastUpdateAt!!.toJavaLocalDateTime().isAfter(nowTime.toJavaLocalDateTime())  ){
                val taskSplit = it.task.split("_")
                if(taskSplit.size == 2){
                    val task = taskSplit[0].trim().lowercase()
                    val param = taskSplit[1].trim().lowercase()
                    when(task){
                        "update" -> {
                            updateTask(param, it.supplierId)
                        }
                    }
                }
            }
        }
    }

    fun start(){
        active = true
        schedulerScope.launch {
            while(active){
                launchTasks()
                //launch once every 30 minutes
                delay(18000000)
            }
        }
    }

    fun stop(){
        active = false
    }

}