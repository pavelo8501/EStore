package adminApi.com.datareader.classes

import kotlinx.serialization.Serializable

@Serializable
data class AuthParam (
    val name:String,
    val value:String
)