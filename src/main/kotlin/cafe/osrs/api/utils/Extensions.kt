package cafe.osrs.api.utils

import cafe.osrs.api.APIConfig
import cafe.osrs.api.clients.ge.GEClientCache
import cafe.osrs.api.clients.hiscore.HiscoreResponse
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.server.application.*
import io.ktor.server.response.*

internal val DEFAULT_LETTERS = ArrayList<Char>().apply {
    addAll(('a'..'z').toList())
    addAll(('A'..'Z').toList())
    addAll(('0'..'9').toList())
}.toList()

fun String.Companion.random(
    length: Int,
    characters: List<Char> = DEFAULT_LETTERS
): String = Array(length) { characters.random() }.joinToString("")

fun HttpClientConfig<*>.addUserAgent() {
    install(UserAgent) {
        agent = APIConfig.userAgent
    }
}

fun getUnixTime() = System.currentTimeMillis() / 1000

fun ApplicationCall.setHiscoreResponseHeaders(response: HiscoreResponse) {
    this.response.header("HS-Cache-QueryTime", response.queryTime)
    this.response.header("HS-Cache-Cached", response.cached.toString())
    this.response.header("HS-Cache-Time-Until-Refresh", response.timeUntilRefresh)
}

fun ApplicationCall.setGEResponseHeaders(response: GEClientCache) {
    //TODO: is there any point in a cached boolean header? Maybe not here since the cache time is usually low unlike hiscores?
    this.response.header("GE-Cache-QueryTime", response.queryTime)
    this.response.header("GE-Cache-Time-Until-Refresh", response.timeUntilRefresh)
}

fun String?.verifyValidCharacterName(): String {
    when {
        isNullOrBlank() -> throw NameEmptyException()
        length > 12 -> throw NameTooLongException(this)
    }
    return this!!
}