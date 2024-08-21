package adminApi.com.models

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive


//interface IDataModel {
//    var id:Int
//    var supplierId:Int
//
//}

//abstract class DataModel()  {
//    var id:Int =0
//    var supplierId:Int = 0
//    abstract val js: JsonData1
//}

//interface IJsonData {
//    var map : HashMap<String,String>?
//    var jsonObject : JsonObject?
//    fun intProp(name:String):Int
//    fun strProp(name:String):String
//
//}

//class JsonData1() :IJsonData {
//   override var map : HashMap<String,String>? = null
//   override var jsonObject : JsonObject? = null
//
//   private fun getValue(name:String):String{
//        if(jsonObject == null){
//            return ""
//        }
//        var key = name
//        if(map != null){
//            key = map!![name] ?: name
//        }
//        val found = jsonObject!![key] ?: throw Exception("Property not found in source data for key $key")
//        return found.jsonPrimitive.content
//    }
//   override fun intProp(name:String):Int{
//        val value = getValue(name)
//        if(value.isEmpty()){
//            return 0
//        }
//        return value.toInt()
//    }
//   override fun strProp(name:String):String{
//        val value = getValue(name)
//        return value
//    }
//
//    fun setMapping(mapping: HashMap<String, String>) {
//        map = mapping
//    }
//    fun  setProviderData(source:JsonElement, mapping: HashMap<String, String>?){
//        this.jsonObject = source.jsonObject
//        this.map = mapping
//    }
//
//    constructor(source: JsonElement) : this() {
//        jsonObject = source.jsonObject
//    }
//}
//
