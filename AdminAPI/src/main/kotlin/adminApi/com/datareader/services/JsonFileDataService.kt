package adminApi.com.datareader.services

import adminApi.com.common.statistics.DataSetType
import adminApi.com.common.statistics.DataType
import adminApi.com.common.statistics.ServiceCallResult
import adminApi.com.datareader.classes.DataServiceCallResult
import adminApi.com.datareader.connectors.ActionConnector
import adminApi.com.datareader.connectors.Connector
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths

class JsonFileDataService(private val connector: Connector) : DataService() {

    init {
        setConnector(connector)
    }

    override suspend fun executeCall(methodName: String): DataServiceCallResult {

        val method = getMethod(methodName)

        val serviceMethodResult = DataServiceCallResult(DataType.JSON,"actionFromFile", methodName, method.tableName)

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

        val response = decodeFormat.decodeFromString<ActionConnector.Response>(responseString)
        if (response.success) {
            serviceMethodResult.setResult(response.data, DataSetType.COMPLETE)
        } else {
            serviceMethodResult.setError(500, response.errorMessage!!)
        }
        return serviceMethodResult
    }

}