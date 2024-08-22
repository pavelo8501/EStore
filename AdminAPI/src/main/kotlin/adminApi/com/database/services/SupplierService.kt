package adminApi.com.database.services

import adminApi.com.database.classes.DBEntity
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.SupplierData
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database

object Suppliers : IntIdTable("suppliers", "supplier_id") {
    val name = varchar("name",62)
    val connectorSettings = text("connector_settings")
}

class SupplierEntity(supplier_id: EntityID<Int>) : IntEntity(supplier_id) {
    companion object : IntEntityClass<SupplierEntity>(Suppliers)
    var name by Suppliers.name
    var connectorSettings by Suppliers.connectorSettings

    fun toModel():SupplierData{

        return SupplierData(id.value,name,connectorSettings)

    }
}

class SupplierService(val db: Database) : DatabaseService(db, SupplierEntity)  {
    override fun create(data: ICommonData): DBEntity {
        TODO("Not yet implemented")
    }

}

