package adminApi.com.database.classes

import adminApi.com.database.services.DatabaseService

class ServiceContainer<out E :DatabaseService > (
    val name :String,
    val service : E,
    val connectionName :String,
)