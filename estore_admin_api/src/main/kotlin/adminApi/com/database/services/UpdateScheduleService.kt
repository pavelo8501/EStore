package adminApi.com.database.services

import adminApi.com.database.classes.DBEntity
import adminApi.com.general.models.data.ICommonData
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.kotlin.datetime.datetime
import org.jetbrains.exposed.sql.transactions.transaction

object UpdateScheduleSettings : IntIdTable("supplier_update_schedule", "id") {
    val supplierId = integer ("supplier_id")
    val containerName =  varchar("container_name",64)
    val updateFrequency =  integer("update_frequency")
    val lastUpdate =  datetime("last_update_at").nullable()
}

class UpdateScheduleEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UpdateScheduleEntity>(UpdateScheduleSettings)
    //var id by UpdateScheduleSettings.id
    var supplierId by UpdateScheduleSettings.supplierId
    var containerName by UpdateScheduleSettings.containerName
    var updateFrequency by UpdateScheduleSettings.updateFrequency
    var lastUpdate by UpdateScheduleSettings.lastUpdate
}

class UpdateScheduleService(val db: Database)  {

    fun select(): List<UpdateScheduleEntity>{
        return  dbQuery{
            UpdateScheduleEntity.all().toList()
        }
    }

    fun pick(id: Int) : UpdateScheduleEntity? {
        val result = dbQuery {
            UpdateScheduleEntity.find { UpdateScheduleSettings.id  eq id }.firstOrNull()
        }
        return result
    }

    fun update(entity: UpdateScheduleEntity) = dbQuery{

        entity.flush()

    }

    fun  <T>dbQuery(body : () -> T): T = transaction(db) {
        body()
    }

}

