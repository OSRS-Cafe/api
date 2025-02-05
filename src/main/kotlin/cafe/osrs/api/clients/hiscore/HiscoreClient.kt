package cafe.osrs.api.clients.hiscore

import cafe.osrs.api.utils.addUserAgent
import cafe.osrs.api.utils.getUnixTime
import cafe.osrs.api.utils.verifyValidCharacterName
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.minutes

object HiscoreClient {
    private val cache = HashMap<HiscoreResponseKey, HiscoreCache>()
    private val client = HttpClient { addUserAgent() }
    private val LOGGER = KtorSimpleLogger("HiscoreClient") //TODO: is this how you do logging in ktor??
    private val CACHE_TIME = APIConfig.hiscoreCacheTimeMinutes.minutes.inWholeSeconds

    fun getHiscoreURL(mode: HiscoreMode, player: String) = "https://secure.runescape.com/m=${mode.endpoint}/index_lite.json?player=$player"

    suspend fun getHiscore(mode: HiscoreMode, player: String): HiscoreResponse {
        player.verifyValidCharacterName()

        val key = HiscoreResponseKey(player, mode)

        val cached = cache[key]

        val (isCached, response) = if(cached == null || getUnixTime() > cached.queryTime + CACHE_TIME) {
            val url = getHiscoreURL(mode, player)
            LOGGER.info("HiscoreUtils.getHiscore called ($url)")
            val response = client.get(url)
            val cachedNew = HiscoreCache(
                status = response.status,
                queryTime = getUnixTime(),
                response = if(response.status == HttpStatusCode.OK) Json.decodeFromString(response.bodyAsText()) else null
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

@Serializable
data class HiscoreResponseDTO(
    val skills: List<HiscoreResponseSkillDTO>,
    val activities: List<HiscoreResponseActivtityDTO>
)

@Serializable
data class HiscoreResponseSkillDTO(
    val id: Int,
    val name: String,
    val rank: Int,
    val level: Int,
    val xp: Int
)

@Serializable
data class HiscoreResponseActivtityDTO(
    val id: Int,
    val name: String,
    val rank: Int,
    val score: Int
)

data class HiscoreResponseKey(
    val player: String,
    val mode: HiscoreMode
)

data class HiscoreCache(
    val status: HttpStatusCode,
    val queryTime: Long,
    val response: HiscoreResponseDTO?
)

data class HiscoreResponse(
    val status: HttpStatusCode,
    val cached: Boolean,
    val queryTime: Long,
    val timeUntilRefresh: Long,
    val response: HiscoreResponseDTO?
)