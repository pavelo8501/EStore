package po.estoreAdminApi.plugins

import io.ktor.server.application.Application
import org.koin.ktor.ext.inject
import po.estoreAdminApi.database.DBManager
import po.estoreAdminApi.datareader.data.ProviderManager
import po.estoreAdminApi.general.DataManager
import po.estoreAdminApi.scheduler.Scheduler
import kotlin.getValue

fun Application.configSystem() {

    val db by inject<DBManager>()

    val providerManager by inject<ProviderManager>()

    val dataManager by inject<DataManager>()
    dataManager.init(db)
    dataManager.loadSuppliers{
        providerManager.initProvidersFromSupplierList(it)
    }

    val scheduler  = Scheduler(db)
    scheduler.registerLaunchFn(1,"producers",providerManager::updateProducers)
    scheduler.registerLaunchFn(1,"categories", providerManager::updateCategories )
    scheduler.registerLaunchFn(1,"products", providerManager::updateProducts)

    scheduler.start()
}