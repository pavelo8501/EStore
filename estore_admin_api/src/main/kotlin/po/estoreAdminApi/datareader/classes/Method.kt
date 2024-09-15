package po.estoreAdminApi.datareader.classes

enum class MethodType {
    GET,
    POST,
    PUT,
    DELETE
}

interface IMethod {

    val name:String
    val path:String
    val tableName :String
    val methodType : MethodType
    val description:String?
    val parameters : Map<String,String>
}


data class Method(
    override val name:String,
    override val path:String,
    override val tableName :String,
    override val methodType: MethodType = MethodType.GET,
    override val description:String?,
    override val parameters : Map<String,String>
) : IMethod