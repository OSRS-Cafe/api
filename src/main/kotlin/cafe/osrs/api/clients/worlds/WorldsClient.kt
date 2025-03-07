package cafe.osrs.api.clients.worlds

import cafe.osrs.api.APIConfig
import cafe.osrs.api.clients.ge.GEClient
import cafe.osrs.api.clients.ge.GEItemMapping
import cafe.osrs.api.utils.ComputedStore
import cafe.osrs.api.utils.createHttpClient
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object WorldsClient {
    private const val WORLDS_LIST_URL = "https://oldschool.runescape.com/slu"
    private val client = createHttpClient("WorldsClient")

    val worldsStore = ComputedStore(cacheTime = APIConfig.worldsListCacheTimeMinutes.minutes) {
        WorldsParser.parse(client.get(WORLDS_LIST_URL).bodyAsText())
    }
}