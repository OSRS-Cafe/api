package cafe.osrs.api.clients.ge

import kotlinx.serialization.Serializable

@Serializable
data class GEVolumes(
    val timestamp: Long,
    val data: HashMap<Int, Int>
)