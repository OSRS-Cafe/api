package cafe.osrs.api.routes

import cafe.osrs.api.APIConfig
import cafe.osrs.api.utils.ComputedStore
import cafe.osrs.api.utils.GenericRSApiException
import cafe.osrs.api.utils.createHttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.time.Duration.Companion.seconds

fun Route.PlayerCountRoute() {
    val url = "https://oldschool.runescape.com"
    val regex = "<p class='player-count'>There are currently ([\\d,]+) people playing!</p>".toRegex()
    val client = createHttpClient(name = "PlayerCountRoute")
    val playerCount = ComputedStore(cacheTime = APIConfig.playerCountCacheTimeSeconds.seconds) {
        val page = client.get(url).bodyAsText()
        regex.find(page)?.groups?.get(1)?.value?.replace(",", "") ?: throw GenericRSApiException("Error checking player count")
    }

    get("/count") {
        call.respondText(playerCount.get())
    }
}