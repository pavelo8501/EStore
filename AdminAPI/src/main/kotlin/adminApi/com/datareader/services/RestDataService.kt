package adminApi.com.datareader.services

import adminApi.com.datareader.classes.Method
import adminApi.com.datareader.classes.MethodType
import adminApi.com.datareader.classes.ServiceMethodResult
import adminApi.com.datareader.connectors.ActionConnector
import adminApi.com.datareader.connectors.Connector
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths

class  RestDataService<T:Connector>(connector : T) : DataService() {

    val client = HttpClient(CIO){
        engine { requestTimeout =  10000 }
    }

    init {
        setConnector(connector)
    }

    override suspend fun executeMethod(methodName: String) : ServiceMethodResult?{
        val method = getMethod(methodName)
        return executeMethod(method)
    }
    suspend fun executeMethod(callMethod: Method) : ServiceMethodResult? {


        val response: HttpResponse = client.request(getUrl() + callMethod.path){

            method = when(callMethod.methodType){
                MethodType.GET -> HttpMethod.Get
                MethodType.POST -> HttpMethod.Post
                MethodType.PUT -> HttpMethod.Put
                MethodType.DELETE -> HttpMethod.Delete
            }

            url {
                callMethod.parameters.forEach {
                    parameters.append(it.key, it.value)
                }
            }

            headers{
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.ContentType, "application/json")
                append("CustomerId", getAuthValue("customerId"))
                append("UserName", getAuthValue("userName"))
                append("ActionApiKey",  getAuthValue("apiKey"))
            }
        }
        if (response.status.value  !in 200..299) {
            println("Response failed!")
            println(response.body<String>())
            return null
        }

        val responseString = response.body<String>()

//        val path = Paths.get("").toAbsolutePath().toString()
//        val writer = File(path+"/static_data/products.json")
//        writer.createNewFile()
//        writer.writeText(responseString)

        val decodeFormat = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

        val result = decodeFormat.decodeFromString<ActionConnector.Response>(responseString)

        if(!result.success){
            throw Exception(result.errorMessage)
        }
        return  ServiceMethodResult(connectorName = "action", methodName = callMethod.name, tableName = callMethod.tableName ,  data = result.data)
    }


}