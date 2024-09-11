package adminApi.com.plugins

import adminApi.com.database.DBManager
import adminApi.com.models.Configuration
import adminApi.com.scheduler.Scheduler
import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import java.io.File
import java.nio.file.Paths


fun Application.configScheduler() {
    val db by inject<DBManager>()
    val scheduler  = Scheduler(db)
    scheduler.start()
}