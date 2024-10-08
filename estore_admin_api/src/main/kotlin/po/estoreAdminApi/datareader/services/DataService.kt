package po.estoreAdminApi.datareader.services

import po.estoreAdminApi.datareader.classes.DataServiceCallResult
import po.estoreAdminApi.datareader.classes.Method
import po.estoreAdminApi.datareader.connectors.Connector


abstract class DataService {

    private var connector : Connector? = null

    fun <T : Connector> setConnector(connector: T){
        this.connector = connector
    }

    fun getMethod(name: String): Method {
        if(connector != null) {
            return connector!!.getMethod(name)
        }
        throw Exception("Connector not set")
    }

    inline fun <T, R> dataSecond(receiver: T, block: T.() -> R): R {
        return receiver.block()
    }

    fun getAuthValue(key: String): String {
        if (connector != null) {
            return connector!!.getAuthParamValue(key)
        }
        throw Exception("Connector not set")
    }

    abstract suspend fun executeCall(methodName: String): DataServiceCallResult
}

