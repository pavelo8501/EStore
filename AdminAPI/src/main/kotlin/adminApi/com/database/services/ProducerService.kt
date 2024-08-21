package adminApi.com.database.services

import adminApi.com.database.classes.DBEntity
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProducerData
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database


object Producers : IntIdTable("producers", "id") {
    val supplierId = integer("supplier_id")
    val providerId = varchar("providerId",64)
    val name = varchar("name",128)
}

class ProducerEntity(id: EntityID<Int>) :DBEntity(id) {
    companion object : IntEntityClass<ProducerEntity>(Producers)

    override var supplierId by Producers.supplierId

    override var providerId   by Producers.providerId
    var name by Producers.name

    override fun toData(): ProducerData {
        return ProducerData(id.value,supplierId,providerId,name)
    }

    override fun fromData(source: ICommonData) {
        name = (source as ProducerData).name
    }

}

class ProducerService(val db: Database) : DatabaseService(db, ProducerEntity)  {

    override fun create(data: ICommonData): DBEntity {
        val  producer = data as ProducerData
        val result = dbQuery {
            ProducerEntity.new {
                supplierId = producer.supplierId
                providerId = producer.providerId
                name = producer.name
            }
        }
        return result
    }

    fun getProducers(): List<ProducerEntity>{
        return super.select<ProducerEntity>()
    }
}