package po.estoreAdminApi.datareader.connectors

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import po.estoreAdminApi.datareader.classes.Method

class ActionConnector() : ConnectorAbstraction() {

    companion object {
        fun categoryMappings(): HashMap<String,String> {
            val result : HashMap<String,String> =  hashMapOf()
            result["categoryId"] = "categoryId"
            result["name"] = "name"
            result["parentCategoryId"] = "parentCategoryId"
            result["language"] = "language"
            return result
        }

        fun producerMappings(): HashMap<String, String> {
            val result : HashMap<String, String> = hashMapOf()
            result["producerId"] = "producerId"
            result["name"] = "name"
            result["description"] = "description"
            return result
        }
    }

    @Serializable
    class Response (
        val success: Boolean,
        val data: List<JsonElement>,
        val errorMessage: String?
    )

    override var name = "action"
    override var url = "https://api.action.pl/api/ade/v2/"

    val columnMappings : HashMap<String,String> = hashMapOf()

    init {

        columnMappings["categoryId"] = "categoryId"
        columnMappings["name"] = "name"
        columnMappings["parentCategoryId"] = "parentCategoryId"
        columnMappings["language"] = "language"

        methods["getCategories"] = Method(
            name = "getCategories",
            path = "ProductCategories",
            tableName = "categories",
            description = "Get all categories",
            parameters = mapOf(Pair("Language", "English"))
        )
        methods["getProducers"] = Method(
            name = "getProducers",
            path = "Producers",
            tableName = "producers",
            description = "Get all producers",
            parameters = mapOf(Pair("Language", "English"))
        )
        methods["getProducts"] = Method(
            name = "getProducts",
            path = "Product/GetAll",
            tableName = "products",
            description = "Get all products",
            parameters = mapOf(Pair("Language", "English"), Pair("Currency","EUR"))
        )
    }


}