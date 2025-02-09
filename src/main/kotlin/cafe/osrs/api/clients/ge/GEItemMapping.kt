package cafe.osrs.api.clients.ge

import kotlinx.serialization.Serializable

@Serializable
data class GEItemMapping(
    val examine: String,
    val id: Int,
    val members: Boolean,
    val lowalch: Int = -1,
    val limit: Int = -1,
    val value: Int,
    val highalch: Int = -1,
    val icon: String,
    val name: String
)