package adminApi.com.managers

import adminApi.com.database.DBManager
import adminApi.com.database.services.SupplierEntity
import adminApi.com.datareader.connectors.ActionConnector
import adminApi.com.datareader.data.ProviderManager
import adminApi.com.general.models.data.SupplierData

class SystemManager(val db:DBManager) {

   // private val databaseManager = DBManager("localhost/estore","root","")
    private val providerManager = ProviderManager()

    private val suppliers: HashMap<String,SupplierData> = hashMapOf()

    private fun initSupplier(supplier: SupplierData): SupplierData?{

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

    fun init(){
       val result =  db.suppliersService().select<SupplierEntity>()
       for (entity in result) {
           var supplier : SupplierData? = entity.toModel()
           if(supplier != null){
               supplier = initSupplier(supplier)
               if (supplier != null){
                   suppliers[supplier.name] = supplier
               }
           }
       }
    }


    fun getSuppliers():List<SupplierData>{
        return suppliers.values.toList()
    }
}