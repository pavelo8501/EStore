package po.estoreAdminApi.database.classes

import po.estoreAdminApi.database.services.DatabaseService


class ServiceContainer<out E : DatabaseService > (
    val name :String,
    val service : E,
    val connectionName :String,
)