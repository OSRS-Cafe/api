package cafe.osrs.api.clients.playercount

import cafe.osrs.api.APIConfig
import cafe.osrs.api.utils.ComputedStore
import cafe.osrs.api.utils.GenericRSApiException
import cafe.osrs.api.utils.createHttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlin.time.Duration.Companion.seconds

object PlayerCountClient {
    private const val WEBSITE_URL = "https://oldschool.runescape.com"
    private val regex = "<p class='player-count'>There are currently ([\\d,]+) people playing!</p>".toRegex()
    private val client = createHttpClient(name = "PlayerCountClient")

    private val playerCountStore = ComputedStore(cacheTime = APIConfig.playerCountCacheTimeSeconds.seconds) {
        val page = client.get(WEBSITE_URL).bodyAsText()
        regex.find(page)?.groups?.get(1)?.value?.replace(",", "") ?: throw GenericRSApiException("Error checking player count")
    }

    suspend fun getPlayerCount() = playerCountStore.get().toInt()
}