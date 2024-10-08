package po.estoreAdminApi.datareader.services


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import po.estoreAdminApi.common.statistics.DataSetType
import po.estoreAdminApi.common.statistics.DataType
import po.estoreAdminApi.datareader.classes.DataServiceCallResult
import po.estoreAdminApi.datareader.classes.MethodType
import po.estoreAdminApi.datareader.connectors.ActionConnector
import po.estoreAdminApi.datareader.connectors.WebConnector

class RestDataService<Connector>(private val connector: WebConnector) : DataService() {

    private val client = HttpClient(CIO) {
        engine { requestTimeout = 10000 }
    }

    init {
        setConnector(connector)
    }

    override suspend fun executeCall(methodName: String): DataServiceCallResult {
        val callMethod = getMethod(methodName)
        val dataCallResult = DataServiceCallResult(
            DataType.JSON,
            connector.name,
            methodName,
            callMethod.tableName,
        )
        val response: HttpResponse = client.request(connector.url + callMethod.path) {

            method = when(callMethod.methodType) {
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

            headers {
                append(HttpHeaders.Accept, "application/json")
                append(HttpHeaders.ContentType, "application/json")
                append("CustomerId", getAuthValue("customerId"))
                append("UserName", getAuthValue("userName"))
                append("ActionApiKey", getAuthValue("apiKey"))
            }
        }
        if (response.status.value !in 200..299) {
            println("Response failed!")
            println(response.body<String>())
            dataCallResult.setError(response.status.value, response.body<String>())
        }

        val responseString = response.body<String>()
        val decodeFormat = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        val apiResponse = decodeFormat.decodeFromString<ActionConnector.Response>(responseString)
        if (!apiResponse.success) {
            dataCallResult.setError(500, apiResponse.errorMessage!!)
        } else {
            dataCallResult.setResult(apiResponse.data, DataSetType.COMPLETE)
        }
        return dataCallResult
    }


}