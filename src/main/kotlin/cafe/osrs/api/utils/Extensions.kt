package cafe.osrs.api.utils

import cafe.osrs.api.APIConfig
import cafe.osrs.api.clients.hiscore.HiscoreResponse
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.server.application.*
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

fun ApplicationCall.setHiscoreResponseHeaders(response: HiscoreResponse) {
    //TODO: Put this into the response json?
    //Later sven: Probably not, this way its very convenient to pass this message for multiple kind of responses
    //We should however make sure to have different cache headers for different kinds of clients (eg: Hiscore vs GE)
    this.response.header("RS-Cache-QueryTime", response.queryTime)
    this.response.header("RS-Cache-Cached", response.cached.toString())
    this.response.header("RS-Cache-Time-Until-Refresh", response.timeUntilRefresh)
    this.response.header("content-type", "application/json")
}

fun String?.verifyValidCharacterName(): String {
    when {
        isNullOrBlank() -> throw NameEmptyException()
        length > 12 -> throw NameTooLongException(this)
    }
    return this!!
}