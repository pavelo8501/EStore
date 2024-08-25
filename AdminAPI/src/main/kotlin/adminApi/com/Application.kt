package adminApi.com

import adminApi.com.database.DBManager

import adminApi.com.datareader.data.ProviderManager
import adminApi.com.general.DataManager

import adminApi.com.plugins.*
import adminApi.com.scheduler.Scheduler
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun startDataReader(){

}

fun start() {

   val db = DBManager("localhost/estore_backend","root","")

   val dataManager = DataManager(db)
   val providerManager = ProviderManager()
   dataManager.loadSuppliers { suppliers ->
      providerManager.initProvidersFromSupplierList(suppliers)
   }

    val scheduler  = Scheduler(db)
    scheduler.registerLaunchFn(1,"producer") {
        providerManager.getProducers("action")
    }
//    scheduler.registerLaunchFn(1,"producer") {
//        providerManager.getProducers("action")
//    }
//    scheduler.registerLaunchFn(1,"category") {
//        providerManager.getCategories("action")
//    }

   dataManager.onDataLoaded = {
       scheduler.start()
   }
   dataManager.init()
}

fun test(){
    
}

fun Application.module() {
    configureHTTP()
    configureSockets()
    configureSerialization()
    configureSecurity()
    configureRouting()
    start()
    //test()
}
