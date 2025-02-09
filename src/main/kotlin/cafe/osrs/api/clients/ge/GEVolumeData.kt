package cafe.osrs.api.clients.ge

import kotlinx.serialization.Serializable

@Serializable
data class GEVolumeData(
    val data: HashMap<Int, GEVolumeDataItem>
) {
    @Serializable
    data class GEVolumeDataItem(
        val high: Int?,
        val highTime: Int?,
        val low: Int?,
        val lowTime: Int?
    )
}