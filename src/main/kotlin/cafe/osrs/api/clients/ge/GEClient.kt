package cafe.osrs.api.clients.ge

import cafe.osrs.api.APIConfig
import cafe.osrs.api.utils.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.logging.*
import java.io.File
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

//TODO: Rename these clients? Should we group clients based on the endpoints they access (probably since they are clients for that) or based on the data they return/calculate?
object GEClient {
    //TODO: The runescape wiki api offers old data based on time, support that!
    private const val URL_VOLUMES = "https://prices.runescape.wiki/api/v1/osrs/volumes"
    private const val URL_MAPPING = "https://prices.runescape.wiki/api/v1/osrs/mapping"
    private const val URL_DATA_LATEST = "https://prices.runescape.wiki/api/v1/osrs/latest"
    private const val URL_ICON = "https://oldschool.runescape.wiki/images/%s"

    private val client = createHttpClient("GEClient")
    private val LOGGER = KtorSimpleLogger("GEClient")
    private val cacheFolder = File(APIConfig.volumePath, "icon-cache").apply { mkdirs() }

    //TODO: Enable a ComputedStore to force refresh if another ComputedStore has refreshed since
    val mappingStore = ComputedStore(cacheTime = 12.hours) {
        client.get(URL_MAPPING).body<List<GEItemMapping>>()
    }
    val volumesStore = ComputedStore(cacheTime = APIConfig.geCacheTimeMinutes.minutes) {
        client.get(URL_VOLUMES).body<GEVolumes>()
    }
    val dataStore = ComputedStore(cacheTime = APIConfig.geCacheTimeMinutes.minutes) {
        client.get(URL_DATA_LATEST).body<GEVolumeData>()
    }
    val itemStore = ComputedStore(cacheTime = APIConfig.geCacheTimeMinutes.minutes) {
        createItems(data = dataStore.get(), mapping = mappingStore.get(), volumes = volumesStore.get())
    }

    //Create a combined Item DTO list with both data and mapping
    private fun createItems(data: GEVolumeData, mapping: List<GEItemMapping>, volumes: GEVolumes): List<GEItem> {
        return mapping.filter { itemMapping ->
            data.data.contains(itemMapping.id).also { if(!it) LOGGER.debug("Couldn't find data for {}", itemMapping) }
        }.map {
            val itemData = data.data[it.id] ?: throw GenericRSApiException("Could not find item with id: ${it.name}")
            GEItem(
                name = it.name,
                examine = it.examine,
                id = it.id,
                members = it.members,
                limit = it.limit,
                value = it.value,
                lowalch = it.lowalch,
                highalch = it.highalch,
                high = itemData.high ?: -1,
                highTime = itemData.highTime ?: -1,
                low = itemData.low ?: -1,
                lowTime = itemData.lowTime ?: -1,
                volume = volumes.data[it.id] ?: -1
            )
        }
    }

    suspend fun getIcon(id: Int, type: IconType): ByteArray {
        val iconBaseFilename = mappingStore.get().firstOrNull { it.id == id }?.icon?.replace(" ", "_") ?: throw ItemIdNotFoundException()
        val realName = File(iconBaseFilename).let { base -> "${base.nameWithoutExtension}${type.addition}.${base.extension}" }
        val cacheFile = File(cacheFolder, realName)
        if(cacheFile.exists()) {
            return cacheFile.readBytes()
        } else {
            val response = client.get(URL_ICON.format(realName)).bodyAsBytes()
            cacheFile.writeBytes(response)
            return response
        }
    }

    enum class IconType(val addition: String) {
        NORMAL(""), DETAIL("_detail")
    }
}