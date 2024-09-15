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

fun Application.module() {
    configureHTTP()
    configureSockets()
    configureSerialization()
    configureSecurity()
    configureRouting()
    configureKoin()
    configSystem()
}
