package adminApi.com.database.services

import adminApi.com.database.classes.DBEntity
import adminApi.com.database.services.Products.integer
import adminApi.com.database.services.Products.varchar
import adminApi.com.general.models.data.ICommonData
import adminApi.com.general.models.data.ProducerData
import adminApi.com.general.models.data.ProductData
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.kotlin.datetime.date
import org.jetbrains.exposed.sql.kotlin.datetime.datetime


object Products : IntIdTable("products", "id") {
    val supplierId = integer("supplier_id")
    val markedForRemovalAt = datetime("marked_for_removal_at").nullable()
    val providerId = varchar("providerId",64)
    val producerId = varchar("producerId",32)
    val producerCode = varchar("producerCode",64)
    val categoriesIds = varchar("categoriesIds",64)
    val name = varchar("name",256)
    val quantity = integer("quantity")
    val price = double("price")
    val currency = varchar("currency",4)
    val ean = varchar("ean",16)
    val sizeX = integer("sizeX")
    val sizeY = integer("sizeY")
    val sizeZ = integer("sizeZ")
    val weight = integer("weight")
    val additionalParameters = varchar("additionalParameters", 128)
}

class ProductEntity(id: EntityID<Int>) :DBEntity(id) {
    companion object : IntEntityClass<ProductEntity>(Products)

    override var supplierId by Products.supplierId
    override var providerId   by Products.providerId
    override var markedForRemovalAt by Products.markedForRemovalAt
    var producerId by Products.producerId
    var producerCode by Products.producerCode
    var categoriesIds by Products.categoriesIds
    var name by Products.name
    var quantity by Products.quantity
    var price by Products.price
    var currency by Products.currency
    var ean by Products.ean
    var sizeX by Products.sizeX
    var sizeY by Products.sizeY
    var sizeZ by Products.sizeZ
    var weight by Products.weight
    var additionalParameters by Products.additionalParameters

    override fun toData(): ProductData {
        val marketForRemoval = markedForRemovalAt != null
        return ProductData(
            id.value,
            supplierId,
            providerId,
            producerId,
            producerCode,
            categoriesIds,
            name,
            quantity,
            price,
            currency,
            ean,
            sizeX,
            sizeY,
            sizeZ,
            weight,
            additionalParameters).apply { markedForRemoval = marketForRemoval }
    }

    override fun fromData(source: ICommonData) {
        val product = (source as ProductData)
        producerId = product.producerId
        producerCode = product.producerCode
        categoriesIds = product.categoriesIds
        name = product.name
        quantity = product.quantity
        price = product.price
        currency = product.currency
        ean = product.ean
        sizeX = product.sizeX
        sizeY = product.sizeY
        sizeZ = product.sizeZ
        weight = product.weight
        additionalParameters = product.additionalParameters
    }

}

class ProductService(val db: Database) : DatabaseService(db, ProductEntity)  {

    init {
        dbQuery{
            if(!Products.exists()){
                SchemaUtils.create(Products)
            }
        }
    }

    override fun create(data: ICommonData): DBEntity {
        val  product = data as ProductData
        val result = dbQuery {
            ProductEntity.new {
                supplierId = product.supplierId
                providerId = product.providerId
                producerId = product.producerId
                producerCode = product.producerCode
                categoriesIds = product.categoriesIds
                name = product.name
                quantity = product.quantity
                price = product.price
                currency = product.currency
                ean = product.ean
                sizeX = product.sizeX
                sizeY = product.sizeY
                sizeZ = product.sizeZ
                weight = product.weight
                additionalParameters = product.additionalParameters
            }
        }
        return result
    }

    fun getProducers(): List<ProductEntity>{
        return super.select<ProductEntity>()
    }
}