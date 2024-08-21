package adminApi.com.database

import adminApi.com.database.classes.ServiceContainer
import adminApi.com.database.services.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DBManager(url:String, user:String, password:String, driver:String? = null) {

    var connected :Boolean = false
    var defaultDriver = "com.mysql.cj.jdbc.Driver"
    private val connections = hashMapOf<String,Database>()

    val services : MutableMap<String,ServiceContainer<DatabaseService>> = mutableMapOf()

    init {
        if(driver != null) {
            this.defaultDriver =  driver
        }
        connect("default", url, user, password, "com.mysql.cj.jdbc.Driver")
    }

    private fun connect(name:String, url:String, user:String,  password:String, driver:String){
        try{
            val jdbcURL = "jdbc:mysql://$url?user=$user&password=$password"
            val databaseConnection = Database.connect(provideDataSource(jdbcURL,driver))
            connections[name] = databaseConnection

            val suppliersService = SupplierService(databaseConnection)
            addService("SupplierService",name,suppliersService)
            val producersService = ProducerService(databaseConnection)
            addService("ProducerService",name,producersService)
            val categoryService = CategoryService(databaseConnection)
            addService("CategoryService",name,categoryService)
            val productService = ProductService(databaseConnection)
            addService("ProductService",name,productService)
            connected = true
        }catch (e:Exception){
            throw Exception("Error connecting to database")
        }
    }
    private fun addService(connectionName:String, name : String,  service : DatabaseService){
        val serviceContainer = ServiceContainer(name,service,connectionName)
        services[connectionName] = serviceContainer
    }

    fun producersService() : ProducerService{
        val container = services["ProducerService"]
        return container?.service as ProducerService
    }

    fun categoryService() : CategoryService{
        val container = services["CategoryService"]
        return container?.service as CategoryService
    }

    fun suppliersService() : SupplierService{
        val container = services["SupplierService"]
        return container?.service as SupplierService
    }

    fun productService() : ProductService{
        val container = services["ProductService"]
        return container?.service as ProductService
    }

    fun connection(name:String = "default"):Database{
        if(connections.containsKey(name)){
            return connections[name]!!
        }else{
            throw Exception("Connection $name not found")
        }
    }

    private fun provideDataSource(url:String,driverClass:String): HikariDataSource {
        val hikariConfig= HikariConfig().apply {
            driverClassName=driverClass
            jdbcUrl=url
            maximumPoolSize=10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(hikariConfig)
    }

    fun <T> dbQuery(body : () -> T): T = transaction(connection()) {
        body()
    }

    suspend fun <T> dbQueryAsync(block: suspend () -> T): T = newSuspendedTransaction(Dispatchers.IO) {
        block()
    }

}