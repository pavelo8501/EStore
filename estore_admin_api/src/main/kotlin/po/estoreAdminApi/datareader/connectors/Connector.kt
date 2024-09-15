package po.estoreAdminApi.datareader.connectors

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import po.estoreAdminApi.datareader.classes.AuthParam
import po.estoreAdminApi.datareader.classes.Method
import java.awt.geom.Line2D

interface Connector {
    var initialized: Boolean
    val authParams: MutableMap<String, AuthParam>
    var name: String
    val methods: MutableMap<String, Method>

    fun getApiKey(): String
    fun getAuthParamValue(key: String): String
    fun setAuthParam(name: String, value: String)
    fun getMethod(name: String): Method
    fun setAuthentication(settings: HashMap<String, String>)
}

interface WebConnector : Connector {
    var url: String
}

abstract class ConnectorAbstraction() : WebConnector {

    override var initialized: Boolean = false
    override val authParams: MutableMap<String, AuthParam> = mutableMapOf()

    abstract override var name: String
    abstract override var url: String

    override val methods: MutableMap<String, Method> = mutableMapOf()

    override fun getApiKey(): String {
        return if (authParams.containsKey("apiKey")) {
            authParams["apiKey"]!!.value
        } else {
            ""
        }
    }

    override fun getAuthParamValue(key: String): String {
        if (authParams.containsKey(key)){
            return authParams[key]!!.value
        } else {
            throw Exception("Auth param $key not found")
        }
    }

    override fun setAuthParam(name: String, value: String) {
        authParams[name] = AuthParam(name, value)
    }

    override fun getMethod(name: String): Method {
        if(methods.containsKey(name)) {
            return methods[name]!!
        }
        throw Exception("Method $name not found in connector")
    }

    override fun setAuthentication(settings: HashMap<String, String>) {
        settings.forEach { authParam ->
            this.setAuthParam(authParam.key, authParam.value)
        }
        initialized = true
    }
}