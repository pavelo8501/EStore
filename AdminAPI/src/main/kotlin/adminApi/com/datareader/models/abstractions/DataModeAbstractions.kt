package adminApi.com.datareader.models.abstractions

import adminApi.com.datareader.models.auxiliary.JsonPropertyConverter
import adminApi.com.general.models.data.ICommonData
import kotlinx.serialization.json.JsonElement


interface IDataModel {
    var id :Int
    var supplierId: Int
    var providerId: String

    fun fromData(data: ICommonData)
    fun toData():ICommonData

    fun strProp(propName :String,parseToString:Boolean = false) :String
    fun intProp(propName :String) :Int
    fun doubleProp(propName :String) :Double
}

abstract class DataModelClass(jsonElement:JsonElement? = null) :IDataModel {

    private val converter  : JsonPropertyConverter = JsonPropertyConverter(jsonElement)

    override var id :Int = 0
    override var supplierId: Int = 0
    override var providerId: String = ""
    var fromDataManager : Boolean = false
    var markedForRemoval : Boolean = false


    override fun strProp(propName :String,parseToString:Boolean) :String{
        return converter.strProp(propName,parseToString)
    }

    override fun intProp(propName :String) :Int{
        return converter.intProp(propName)
    }

    override fun doubleProp(propName :String) :Double{
        return converter.doubleProp(propName)
    }

}