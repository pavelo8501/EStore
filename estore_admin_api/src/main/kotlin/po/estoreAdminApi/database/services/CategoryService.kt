package po.estoreAdminApi.database.services

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.transactions.transaction
import po.estoreAdminApi.database.classes.DBEntity
import po.estoreAdminApi.general.models.data.CategoryData
import po.estoreAdminApi.general.models.data.ICommonData

object Categories : IntIdTable("categories", "id") {
    val supplierId = integer("supplier_id")
    val markedForRemovalAt = date("marked_for_removal_at").nullable()
    val providerId = varchar("providerId",64)
    val parentCategoryId = integer("parentCategoryId")
    val name = varchar("name",128)
}

class CategoryEntity(id: EntityID<Int>) : DBEntity(id) {
    companion object : IntEntityClass<CategoryEntity>(Categories)
    override var supplierId  by Categories.supplierId
    override var markedForRemovalAt by Categories.markedForRemovalAt
    override var providerId by Categories.providerId
    var parentCategoryId by Categories.parentCategoryId
    var name by Categories.name

    override fun toData() : CategoryData {
        val marketForRemoval = markedForRemovalAt != null
        return CategoryData(
            id.value,
            supplierId,
            providerId,
            parentCategoryId,
            name
        ).apply { markedForRemoval = marketForRemoval }
    }

    override fun fromData(source: ICommonData) {
        parentCategoryId  = (source as CategoryData).parentCategoryId
        name = (source as CategoryData).name
    }
}

class CategoryService(val db:Database) : DatabaseService(db, CategoryEntity) {


    init {
        dbQuery{
            if(!Categories.exists()){
                SchemaUtils.create(Categories)
            }
        }
    }

    override fun create(data: ICommonData): DBEntity {
        val  category = data as CategoryData
        val result = dbQuery {
            CategoryEntity.new {
                supplierId = category.supplierId
                providerId = category.providerId
                parentCategoryId = category.parentCategoryId
                name = category.name
            }
        }
        return result
    }

    fun getCategories(): List<CategoryEntity>{
        return super.select<CategoryEntity>()
    }
}
