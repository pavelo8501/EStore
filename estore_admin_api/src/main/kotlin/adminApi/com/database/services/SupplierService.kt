package adminApi.com.database.services

import adminApi.com.database.classes.DBEntity
import adminApi.com.database.services.Products.nullable
import adminApi.com.general.models.data.ICommonData
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.kotlin.datetime.date

object Suppliers : IntIdTable("suppliers", "supplier_id") {
    val name = varchar("name",62)
    val connectorSettings = text("connector_settings")
}

class SupplierEntity(supplier_id: EntityID<Int>) : IntEntity(supplier_id) {
    companion object : IntEntityClass<SupplierEntity>(Suppliers)
    var name by Suppliers.name
    var connectorSettings by Suppliers.connectorSettings
}

class SupplierService(val db: Database) : DatabaseService(db, SupplierEntity)  {

    init {
        dbQuery{
            if(!Suppliers.exists()){
                SchemaUtils.create(Suppliers)
            }
        }
    }

    override fun create(data: ICommonData): DBEntity {
        TODO("Not yet implemented")
    }

}

