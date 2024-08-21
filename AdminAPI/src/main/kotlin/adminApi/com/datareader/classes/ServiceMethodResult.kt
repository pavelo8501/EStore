package adminApi.com.datareader.classes

import kotlinx.serialization.json.JsonElement

class ServiceMethodResult (

    val connectorName:String,
    val methodName : String,
    val tableName : String,
    val data : List<JsonElement> = emptyList(),

)