package adminApi.com.general.models.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


data class SupplierData (
    var supplierId: Int,
    var name: String = "",
    var connectorSettings: String = ""
){

    val settings : HashMap<String,String> = hashMapOf()

    init {
        val obj = Json.parseToJsonElement(connectorSettings)
        obj.jsonObject.forEach {
            settings[it.key] = it.value.jsonPrimitive.content
        }
    }

}