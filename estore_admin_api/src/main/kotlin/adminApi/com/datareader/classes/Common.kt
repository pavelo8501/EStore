package adminApi.com.datareader.classes

import kotlinx.serialization.Serializable


@Serializable
data class ConnectorSettings(
    val apiKey:String?,
    val endpointAddress:String?,
)