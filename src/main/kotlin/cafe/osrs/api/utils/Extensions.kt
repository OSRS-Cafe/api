package cafe.osrs.api.utils

import cafe.osrs.api.APIConfig
import cafe.osrs.api.clients.hiscore.HiscoreResponse
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

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

fun RoutingContext.setHiscoreResponseHeaders(response: HiscoreResponse) {
    //TODO: Put this into the response json?
    call.response.header("RS-Cache-QueryTime", response.queryTime)
    call.response.header("RS-Cache-Cached", response.cached.toString())
    call.response.header("RS-Cache-Time-Until-Refresh", response.timeUntilRefresh)
    call.response.header("content-type", "application/json")
}

fun String?.verifyValidCharacterName(): String {
    when {
        isNullOrBlank() -> throw NameEmptyException()
        length > 12 -> throw NameTooLongException(this)
    }
    return this!!
}