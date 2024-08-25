package adminApi.com.general.models

import adminApi.com.database.services.DatabaseService
import adminApi.com.database.services.SupplierEntity
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject

class SupplierElement(source : SupplierEntity) {

    var supplierId: Int
    var name: String = ""
    var connectorSettings: String = ""
    var settings : HashMap<String,String> = hashMapOf()

    init {
        supplierId = source.id.value
        name = source.name
        connectorSettings = source.connectorSettings

        val jasonArray = Json.decodeFromString<JsonArray>(connectorSettings)
        jasonArray.forEach(){
            val obj = it.jsonObject
            val key =  obj.keys.firstOrNull()
            if(key != null){
                settings[key] = obj[key].toString()
            }
        }
    }

}