package adminApi.com.database.services

import adminApi.com.database.classes.DBEntity
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProducerData
import kotlinx.datetime.Clock
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.util.*

abstract class DatabaseService(private val database: Database, private val entity : IntEntityClass<IntEntity> ) {
    abstract fun create(data: ICommonData) : DBEntity
    open fun <T:IntEntity>select() : List<T> {
        val result = dbQuery{
            entity.all().toList()
        }
        return  result  as List<T>
    }
    fun pick(id: Int) : IntEntity? {
        val result = dbQuery {
            entity.find(entity.table.id  eq id).firstOrNull()
        }
        return result
    }

    fun update(entity: DBEntity, markForRemoval:Boolean = false){
        if(!markForRemoval){
            entity.markedForRemovalAt = null
        }else{
            if (entity.markedForRemovalAt == null){
                val now: LocalDate = Clock.System.todayIn(TimeZone.UTC)
                entity.markedForRemovalAt = now
            }
        }
    }

    fun  <T>dbQuery(body : () -> T): T = transaction(database) {
        body()
    }

}