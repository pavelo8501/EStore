package adminApi.com.datareader.models.abstractions

import adminApi.com.general.models.data.ICommonData


interface IDataModel {
    var id :Int
    var supplierId: Int
    var providerId: String
}

abstract class DataModelClass :IDataModel {

    override var id :Int = 0
    override var supplierId: Int = 0
    override var providerId: String = ""
    abstract fun toData():ICommonData

    var fromDataManager : Boolean = false

}