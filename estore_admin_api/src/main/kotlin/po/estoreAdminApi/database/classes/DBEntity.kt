package po.estoreAdminApi.database.classes

import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import po.estoreAdminApi.general.models.data.ICommonData


abstract class DBEntity(id: EntityID<Int>) :IntEntity(id) {

    val entityId: Int
        get() = super.id.value
    abstract var supplierId : Int
    // id as provided by the data provider (i.e Supplier)
    abstract var providerId : String
    abstract var markedForRemovalAt : LocalDate?
    var markedForRemoval : Boolean
        get() = markedForRemovalAt != null
        set(value) {
            if(value){
                if(markedForRemovalAt == null){
                    markedForRemovalAt = LocalDate.Companion.parse(Clock.System.now().toLocalDateTime(TimeZone.UTC).toString())
                }
            }
        }


    abstract fun toData(): ICommonData
    abstract fun fromData(source: ICommonData)

}