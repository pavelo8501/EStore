package po.estoreAdminApi.plugins

import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import org.koin.ktor.plugin.Koin
import po.estoreAdminApi.database.DBManager
import po.estoreAdminApi.datareader.data.ProviderManager
import po.estoreAdminApi.general.DataManager
import po.estoreAdminApi.models.Configuration
import java.io.File
import java.nio.file.Paths

fun Application.configureKoin() {

    val appPath = Paths.get("").toAbsolutePath().toString()
    var filePath = appPath+File.separator+"conf.json"
    val confFile = File(filePath)
    if (!confFile.exists()) {
        throw Exception("No configuration file present at $filePath")
    }

    val confRaw = confFile.readText(Charsets.UTF_8)
    val config = Json.decodeFromString<Configuration>(confRaw)

    try {
        val dbManager = org.koin.dsl.module {
            single { DBManager("${config.url}${config.database.db}",config.database.user,config.database.pwd) }
        }

        val dataManager = org.koin.dsl.module {
            single { DataManager() }
        }

        val providerManager  = org.koin.dsl.module {
            single { ProviderManager()}
        }

        install(Koin) {
            modules(dbManager)
            modules(dataManager)
            modules(providerManager)
        }
    } catch (e: Exception) {
        throw Exception("Error while configuring Koin: ${e.message}")
    }

}