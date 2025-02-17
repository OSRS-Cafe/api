package cafe.osrs.api.clients.ge

import kotlinx.serialization.Serializable

@Serializable
data class GEItem(
    val name: String,
    val examine: String,
    val id: Int,
    val members: Boolean,
    val limit: Int,
    val value: Int,
    val lowalch: Int,
    val highalch: Int,
    val high: Int,
    val highTime: Int,
    val low: Int,
    val lowTime: Int,
    val volume: Int
)