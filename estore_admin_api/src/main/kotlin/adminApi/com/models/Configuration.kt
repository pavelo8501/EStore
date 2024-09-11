package adminApi.com.models

import kotlinx.serialization.Serializable


@Serializable
data class DBConnectionSettings(
    val name: String,
    val db : String,
    val user: String,
    val pwd : String,
)

@Serializable
data class Configuration(
    val url : String,
    val database : DBConnectionSettings,
)