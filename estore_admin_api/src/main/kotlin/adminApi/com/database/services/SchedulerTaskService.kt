package adminApi.com.database.services

import adminApi.com.database.classes.DBEntity
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProductData
import adminApi.com.general.models.data.SchedulerTaskData
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime

object SchedulerTasks : IntIdTable("scheduler_tasks", "id") {
    val supplier_id = integer("supplier_id")
    val task = varchar("task",64)
    val frequency = integer("frequency")
    val last_update_at = datetime("last_update_at")
}

class SchedulerTaskEntity(id: EntityID<Int>
) :DBEntity(id) {
    companion object : IntEntityClass<SchedulerTaskEntity>(SchedulerTasks)

    override var supplierId by SchedulerTasks.supplier_id
    var task by SchedulerTasks.task
    var frequency by SchedulerTasks.frequency
    var lastUpdateAt by SchedulerTasks.last_update_at
    override var providerId  = "-"
    override var markedForRemovalAt  = Clock.System.now().toLocalDateTime(TimeZone.UTC)

    override fun toData(): SchedulerTaskData {
        return SchedulerTaskData(
            id.value,
            supplierId,
            task,
            frequency,
            lastUpdateAt
        )
    }

    fun fromSchedulerTaskData(source: SchedulerTaskData) {
        supplierId = source.supplierId
        task = source.task
        frequency = source.frequency
        lastUpdateAt = source.lastUpdateAt
    }

    override fun fromData(source: ICommonData) = fromSchedulerTaskData(source as SchedulerTaskData)

}


class SchedulerTaskService(val db: Database) : DatabaseService(db, ProductEntity)  {
    override fun create(data: ICommonData): DBEntity {
        TODO("Not yet implemented")
    }

    val tasks = mutableListOf<SchedulerTaskData>()

    init {
        dbQuery{
            if(!SchedulerTasks.exists()){
                SchemaUtils.create(SchedulerTasks)
            }
        }

        dbQuery {
            SchedulerTaskEntity.all().forEach {
                tasks.add(it.toData())
            }
        }
    }


    fun getTasks(): List<SchedulerTaskData>{
        return tasks
    }

}