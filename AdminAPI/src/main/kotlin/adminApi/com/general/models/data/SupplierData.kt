package adminApi.com.general.models.data

import adminApi.com.datareader.classes.AuthParam
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*


data class SupplierData (
    var supplierId: Int,
    var name: String = "",
    var connectorSettings: String = ""

    ){

    var settings : HashMap<String,String> = hashMapOf()
    init {

        val jasonArray = Json.decodeFromString<JsonArray>(connectorSettings)
        jasonArray.forEach(){
            val obj = it.jsonObject
            val key =  obj.keys.firstOrNull()
            if(key != null){
                settings[key] = obj[key].toString()
            }
        }

        val a = 10

    }

}