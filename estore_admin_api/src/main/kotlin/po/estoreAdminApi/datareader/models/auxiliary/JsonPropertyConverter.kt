package po.estoreAdminApi.datareader.models.auxiliary

import kotlinx.serialization.json.*
import kotlin.reflect.typeOf

class JsonPropertyConverter(var jsonElement: JsonElement? ) {

        var propertyMap : HashMap<String,String>? = null
        var jsonData : JsonObject? = null

        init {
            if(jsonElement != null){
                jsonData = jsonElement!!.jsonObject
            }
        }

        private fun getValue(name:String,parseToString : Boolean = false):String{
            jsonData ?: "0"
            try {
                var key = name
                if(propertyMap != null){
                    key = propertyMap!![name] ?: name
                }
                val found = jsonData!![key] ?: throw Exception("Property not found in source data for key $key")
                if(parseToString){
                     if(found is JsonArray){
                        return Json.encodeToString(JsonArray.serializer(),(found as JsonArray))
                     }else{
                         return  ""
                     }
                }else{
                    return found.jsonPrimitive.content
                }
            }catch (ex: Exception){
                val a =10
                return ""
            }
        }

        fun intProp(name:String):Int{
            val value = getValue(name)
            if(value.isEmpty()){
                return 0
            }
            return value.toInt()
        }
         fun strProp(name:String,parseToString:Boolean = false):String{
            var value = getValue(name,parseToString)
            value =  value.trim()
            return value
        }
        fun doubleProp(name:String):Double{
            val value = getValue(name)
            if(value.isEmpty()){
                return 0.0
            }
            return value.toDouble()
        }

        fun setProviderData(source: JsonElement, mapping: HashMap<String, String>? = null){
            this.jsonData = source.jsonObject
            this.propertyMap = mapping
        }

}