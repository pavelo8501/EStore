package adminApi.com.database.services

import adminApi.com.general.models.data.ICommonData
import adminApi.com.scheduler.models.SchedulerTask
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object SchedulerTasks : IntIdTable("scheduler_tasks", "id") {
    val supplier_id = integer("supplier_id")
    val task = varchar("task",64)
    val frequency = integer("frequency")
    val last_update_at = datetime("last_update_at").nullable()
}

class SchedulerTaskEntity(id: EntityID<Int>
) : IntEntity(id) {
    companion object : IntEntityClass<SchedulerTaskEntity>(SchedulerTasks)
    var supplierId by SchedulerTasks.supplier_id
    var task by SchedulerTasks.task
    var frequency by SchedulerTasks.frequency
    var lastUpdateAt by SchedulerTasks.last_update_at

    fun toData(): SchedulerTask {
        return SchedulerTask(
            this
        )
    }

    fun fromSchedulerTaskData(source: SchedulerTask) {
        supplierId = source.supplierId
        task = source.task
        frequency = source.frequency
        lastUpdateAt = source.lastUpdateAt
    }

    fun fromData(source: ICommonData) = fromSchedulerTaskData(source as SchedulerTask)

}


class SchedulerTaskService(val db: Database)  {

    fun create(data: ICommonData): SchedulerTaskEntity {
       val taskEntity = dbQuery {
              SchedulerTaskEntity.new {
                fromData(data)
              }
         }
          return taskEntity
       }

    val tasks = mutableListOf<SchedulerTask>()

    init {
        try {
            dbQuery {
                if (!SchedulerTasks.exists()) {
                    SchemaUtils.create(SchedulerTasks)
                }
            }
        } catch (e: Exception) {
            println("Error: $e")
        }

        try {
            dbQuery {
                SchedulerTaskEntity.all().forEach {
                    tasks.add(it.toData())
                }
            }
        } catch (e: Exception) {
            println("Error: $e")
        }
    }

    fun getTaskList(): List<SchedulerTask>{
        return tasks
    }

    fun  <T>dbQuery(body : () -> T): T = transaction(db) {
        body()
    }

}