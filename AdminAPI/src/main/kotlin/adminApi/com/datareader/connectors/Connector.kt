package adminApi.com.datareader.connectors

import adminApi.com.datareader.classes.AuthParam
import adminApi.com.datareader.classes.ConnectorSettings
import adminApi.com.datareader.classes.IMethod
import adminApi.com.datareader.classes.Method
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.awt.geom.Line2D

abstract class Connector {

    var initialized : Boolean = false
    val authParams : MutableMap<String,AuthParam> = mutableMapOf()

    abstract var name : String
    abstract var url : String

    val methods : MutableMap<String, Method> = mutableMapOf()

    fun getApiKey() : String {
        if(authParams.containsKey("apiKey")){
            return authParams["apiKey"]!!.value
        }else{
            return ""
        }
    }

    fun getAuthValue(key:String) : String{
        if(authParams.containsKey(key)){
            return authParams[key]!!.value
        }else{
            throw Exception("Auth param $key not found")
        }
    }
    fun setAuthParam(name:String, value:String){
        authParams[name] = AuthParam(name, value)
    }


    fun getMethod(name:String) : Method {
        if(methods.containsKey(name)){
            return methods[name]!!
        }
        throw Exception("Method $name not found in connector")
    }

    fun setAuthentication( settings: HashMap<String,String>){
        settings.forEach { authParam ->
            this.setAuthParam(authParam.key, authParam.value)
        }
        initialized = true
    }

}