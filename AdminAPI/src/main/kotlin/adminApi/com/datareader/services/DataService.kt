package adminApi.com.datareader.services

import adminApi.com.datareader.classes.Method
import adminApi.com.datareader.classes.ServiceMethodResult
import adminApi.com.datareader.connectors.Connector


abstract class DataService () {

    private var connector : Connector? = null

    fun <T:Connector>setConnector(connector : T){
        this.connector = connector
    }

    fun getMethod(name:String) : Method {
        if(connector != null){
            return connector!!.getMethod(name)
        }
        throw Exception("Connector not set")
    }

    fun getUrl() : String {
        if(connector != null){
            return connector!!.url!!
        }
        throw Exception("Connector not set")
    }

    fun getAuthValue(key:String) : String{
        if(connector != null){
            return connector!!.getAuthValue(key)
        }
        throw Exception("Connector not set")
    }

    abstract suspend fun executeMethod(methodName: String) : ServiceMethodResult?

}