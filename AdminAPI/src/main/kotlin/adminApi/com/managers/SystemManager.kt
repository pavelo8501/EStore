package adminApi.com.managers

import adminApi.com.database.DBManager
import adminApi.com.database.services.SupplierEntity
import adminApi.com.datareader.connectors.ActionConnector
import adminApi.com.datareader.data.ProviderManager
import adminApi.com.general.models.SupplierElement

class SystemManager(val db:DBManager) {

   // private val databaseManager = DBManager("localhost/estore","root","")
    private val providerManager = ProviderManager()

    private val suppliers: HashMap<String,SupplierElement> = hashMapOf()

    private fun initSupplier(supplier: SupplierElement): SupplierElement?{

        val provider = when(supplier.name){
            "action" -> providerManager.createDataProvider<ActionConnector>(supplier.supplierId,supplier.name)
            else -> null
        }
        if (provider == null) {
           return null
        }
        return supplier
    }


    private fun notifyProvidersDataChanged(){

    }


    fun getSuppliers():List<SupplierElement>{
        return suppliers.values.toList()
    }
}