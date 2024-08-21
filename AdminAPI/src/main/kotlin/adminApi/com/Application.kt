package adminApi.com

import adminApi.com.database.DBManager
import adminApi.com.database.classes.DBEntity
import adminApi.com.database.services.ProducerEntity
import adminApi.com.database.services.SupplierEntity
import adminApi.com.datareader.data.ProviderManager
import adminApi.com.general.DataManager
import adminApi.com.general.models.ElementClass
import adminApi.com.general.models.ProducerElement
import adminApi.com.general.models.data.ProducerData
import adminApi.com.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun startDataReader(){

}

fun test() {
   val db = DBManager("localhost/estore","root","")

   val dataManager = DataManager(db)
   val providerManager = ProviderManager()
   dataManager.loadSuppliers { suppliers ->
      providerManager.initProvidersFromSupplierList(suppliers)
   }
   dataManager.init()
   providerManager.getProducers("action")
   //providerManager.getCategories("action")
   //providerManager.getProducts("action")

   runBlocking {
       delay(8000)
       providerManager.getProducers("action")
     //  providerManager.getCategories("action")
     //  providerManager.getProducts("action")
   }

}

fun testDB(){


}

fun Application.module() {
    configureHTTP()
    configureSockets()
    configureSerialization()
    configureSecurity()
    configureRouting()
   // configureSuppliers()
    test()
}
