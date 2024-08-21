package adminApi.com.general.models.data

import kotlinx.serialization.Serializable

@Serializable
sealed interface ICommonData{
    var id :Int
    var supplierId : Int
    var providerId : String
}
