package cafe.osrs.api.clients.ge

data class GEClientCache(
    val queryTime: Long,
    val timeUntilRefresh: Long,
    val mapping: List<GEItemMapping>,
    val data: GEVolumeData,
    val items: List<GEItem>
)