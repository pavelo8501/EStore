package adminApi.com.datareader.connectors

import adminApi.com.datareader.classes.AuthParam
import adminApi.com.datareader.classes.ConnectorSettings
import adminApi.com.datareader.classes.IMethod
import adminApi.com.datareader.classes.Method

abstract class Connector {

    var initialized : Boolean = false
    val authParams : MutableMap<String,AuthParam> = mutableMapOf()

    open var name : String = ""

    open var _apiKey : String? = null

    open var url : String? = null

    val methods : MutableMap<String, Method> = mutableMapOf()

    fun getApiKey() : String {
        if(_apiKey == null){
            throw Exception("Api key not set")
        }else{
            return _apiKey!!
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

    fun setSettings(settings: ConnectorSettings) {
        if(_apiKey == null){
            _apiKey = settings.apiKey
        }
        if(url == null){
            url = settings.endpointAddress
        }
        initialized = true
    }

    fun getMethod(name:String) : Method {
        if(methods.containsKey(name)){
            return methods[name]!!
        }
        throw Exception("Method $name not found in connector")
    }

}