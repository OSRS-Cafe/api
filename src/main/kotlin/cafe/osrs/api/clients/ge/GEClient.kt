package cafe.osrs.api.clients.ge

import cafe.osrs.api.APIConfig.geCacheTimeMinutes
import cafe.osrs.api.utils.GenericRSApiException
import cafe.osrs.api.utils.ItemIdNotFoundException
import cafe.osrs.api.utils.addUserAgent
import cafe.osrs.api.utils.getUnixTime
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.logging.*
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.time.Duration.Companion.minutes

object GEClient {
    private const val URL_VOLUMES = "https://prices.runescape.wiki/api/v1/osrs/volumes"
    private const val URL_MAPPING = "https://prices.runescape.wiki/api/v1/osrs/mapping"
    private const val URL_DATA_LATEST = "https://prices.runescape.wiki/api/v1/osrs/latest"
    private const val URL_ICON = "https://oldschool.runescape.wiki/images/%s"

    private val client = HttpClient { addUserAgent() }
    private val LOGGER = KtorSimpleLogger("GEClient")
    private val json = Json { encodeDefaults = true }
    private val cacheFolder = File(".cache").apply { mkdirs() }

    private var geClientCache: GEClientCache? = null

    suspend fun getIcon(id: Int, type: IconType): ByteArray {
        if(geClientCache == null) refreshCache() //Only refresh if we didnt do it yet, we dont need to update data for images
        //TODO: Cache files in ram? ram more expensive then the cpu time to read a file?
        val iconBaseFilename = geClientCache?.mapping?.firstOrNull { it.id == id }?.icon?.replace(" ", "_") ?: throw ItemIdNotFoundException()
        val realName = File(iconBaseFilename).let { base -> "${base.nameWithoutExtension}${type.addition}.${base.extension}" }
        val cacheFile = File(cacheFolder, realName)
        if(cacheFile.exists()) {
            return cacheFile.readBytes()
        } else {
            val response = callIcon(realName)
            cacheFile.writeBytes(response)
            return response
        }
    }

    suspend fun getMapping(): List<GEItemMapping> {
        refreshCache()
        return geClientCache!!.mapping
    }

    suspend fun getDataLatest(): GEVolumeData {
        refreshCache()
        return geClientCache!!.data
    }

    suspend fun getItems(): List<GEItem> {
        refreshCache()
        return geClientCache!!.items
    }

    suspend fun getGEClientCache(): GEClientCache {
        refreshCache()
        return geClientCache!!
    }

    //Refreshes the cache if its null or the timeUntilRefresh is passed
    private suspend fun refreshCache() {
        if(geClientCache == null || geClientCache!!.timeUntilRefresh < getUnixTime()) {
            val data = callDataLatest()
            val mapping = callMapping()
            val items = createItems(data = data, mapping = mapping)
            geClientCache = GEClientCache(queryTime = getUnixTime(), timeUntilRefresh = getUnixTime() + geCacheTimeMinutes.minutes.inWholeSeconds, data = data, mapping = mapping, items = items)
        }
    }

    private suspend fun callIcon(name: String): ByteArray {
        val url = URL_ICON.format(name)
        LOGGER.info("Called $url")
        return client.get(url).bodyAsBytes()
    }

    private suspend fun callMapping(): List<GEItemMapping> {
        LOGGER.info("Called: $URL_MAPPING")
        return json.decodeFromString(client.get(URL_MAPPING).body())
    }

    private suspend fun callDataLatest(): GEVolumeData {
        LOGGER.info("Called: $URL_DATA_LATEST")
        return json.decodeFromString(client.get(URL_DATA_LATEST).body())
    }

    //Create a combined Item DTO list with both data and mapping
    private fun createItems(data: GEVolumeData, mapping: List<GEItemMapping>): List<GEItem> {
        return mapping.filter { data.data.contains(it.id) }.map {
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
                lowTime = itemData.lowTime ?: -1
            )
        }
    }

    enum class IconType(val addition: String) {
        NORMAL(""), DETAIL("_detail")
    }
}