package adminApi.com.datareader.services

import adminApi.com.datareader.classes.Method
import adminApi.com.datareader.classes.MethodType
import adminApi.com.datareader.classes.ServiceMethodResult
import adminApi.com.datareader.connectors.ActionConnector
import adminApi.com.datareader.connectors.Connector
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.headers
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths

class JsonFileDataService<T: Connector>(connector : T)  : DataService() {

    init {
        setConnector(connector)
    }

    override suspend fun executeMethod(methodName: String) : ServiceMethodResult?{
        val method = getMethod(methodName)

        var path = Paths.get("").toAbsolutePath().toString()
        when(methodName){
            "getProducers" -> path += "/static_data/producers.json"
            "getCategories"-> path += "/static_data/category.json"
            "getProducts" -> path += "/static_data/products.json"
        }

        val reader = File(path)
        val responseString = reader.readText()

        val decodeFormat = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        val result = decodeFormat.decodeFromString<ActionConnector.Response>(responseString)

        if(!result.success){
            throw Exception(result.errorMessage)
        }
        return  ServiceMethodResult(connectorName = "action", methodName = methodName, tableName = "producers" ,  data = result.data)
    }

}