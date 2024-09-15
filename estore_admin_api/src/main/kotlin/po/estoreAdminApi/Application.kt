package po.estoreAdminApi


import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import po.estoreAdminApi.plugins.configSystem
import po.estoreAdminApi.plugins.configureHTTP
import po.estoreAdminApi.plugins.configureKoin
import po.estoreAdminApi.plugins.configureRouting
import po.estoreAdminApi.plugins.configureSecurity
import po.estoreAdminApi.plugins.configureSerialization
import po.estoreAdminApi.plugins.configureSockets


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
