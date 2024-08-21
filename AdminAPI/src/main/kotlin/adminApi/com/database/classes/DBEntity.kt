package adminApi.com.database.classes

import adminApi.com.database.services.Producers
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProducerData
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


abstract class DBEntity(id: EntityID<Int>) :IntEntity(id) {

    val entityId: Int
        get() = super.id.value
    abstract var supplierId : Int
    // id as provided by the data provider (i.e Supplier)
    abstract var providerId : String

    abstract fun toData(): ICommonData
    abstract fun fromData(source: ICommonData)

}