package cafe.osrs.api.clients.hiscore

import cafe.osrs.api.utils.NameEmptyException
import cafe.osrs.api.utils.NameTooLongException
import cafe.osrs.api.utils.addUserAgent
import cafe.osrs.api.utils.getUnixTime
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlin.time.Duration.Companion.minutes

object HiscoreClient {
    private val cache = HashMap<HiscoreResponseKey, HiscoreCache>()
    private val client = HttpClient { addUserAgent() }
    private val CACHE_TIME = 10.minutes.inWholeSeconds //TODO: Make this configurable
    private val LOGGER = KtorSimpleLogger("HiscoreClient") //TODO: is this how you do logging in ktor??

    fun getHiscoreURL(mode: HiscoreMode, player: String) = "https://secure.runescape.com/m=${mode.endpoint}/index_lite.json?player=$player"

    suspend fun getHiscore(mode: HiscoreMode, player: String): HiscoreResponse {
        if(player.length > 12) throw NameTooLongException(player)
        if(player.isBlank()) throw NameEmptyException()

        val key = HiscoreResponseKey(player, mode)

        val cached = cache[key]

        val (isCached, response) = if(cached == null || getUnixTime() > cached.queryTime + CACHE_TIME) {
            val url = getHiscoreURL(mode, player)
            LOGGER.info("HiscoreUtils.getHiscore called ($url)")
            val response = client.get(url)
            val cachedNew = HiscoreCache(
                status = response.status,
                queryTime = getUnixTime(),
                response = if(response.status == HttpStatusCode.OK) response.bodyAsText() else null
            )
            cache[key] = cachedNew
            Pair(false, cachedNew)
        } else {
            Pair(true, cached)
        }

        return HiscoreResponse(
            status = response.status,
            cached = isCached,
            queryTime = response.queryTime,
            timeUntilRefresh = (response.queryTime + CACHE_TIME) - getUnixTime(),
            response = response.response
        )
    }
}

data class HiscoreResponseKey(
    val player: String,
    val mode: HiscoreMode
)

data class HiscoreCache(
    val status: HttpStatusCode,
    val queryTime: Long,
    val response: String?
)

data class HiscoreResponse(
    val status: HttpStatusCode,
    val cached: Boolean,
    val queryTime: Long,
    val timeUntilRefresh: Long,
    val response: String?
)