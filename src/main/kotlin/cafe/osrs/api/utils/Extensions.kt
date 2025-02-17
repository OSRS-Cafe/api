package cafe.osrs.api.utils

import cafe.osrs.api.APIConfig
import cafe.osrs.api.clients.hiscore.HiscoreResponse
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.logging.*
import kotlinx.serialization.json.Json
import java.net.URI
import java.text.DecimalFormat

internal val DEFAULT_LETTERS = ArrayList<Char>().apply {
    addAll(('a'..'z').toList())
    addAll(('A'..'Z').toList())
    addAll(('0'..'9').toList())
}.toList()

fun String.Companion.random(
    length: Int,
    characters: List<Char> = DEFAULT_LETTERS
): String = Array(length) { characters.random() }.joinToString("")

fun getUnixTime() = System.currentTimeMillis() / 1000

fun ApplicationCall.setHiscoreResponseHeaders(response: HiscoreResponse) {
    this.response.header("HS-Cache-QueryTime", response.queryTime)
    this.response.header("HS-Cache-Cached", response.cached.toString())
    this.response.header("HS-Cache-Time-Until-Refresh", response.timeUntilRefresh)
}

fun String?.verifyValidCharacterName(): String {
    when {
        isNullOrBlank() -> throw NameEmptyException()
        length > 12 -> throw NameTooLongException(this)
    }
    return this!!
}

fun createHttpClient(name: String): HttpClient {
    return HttpClient {
        val logger = KtorSimpleLogger(name)
        ResponseObserver { response ->
            logger.info("Called ${response.request.url}")
        }
        install(UserAgent) {
            agent = APIConfig.userAgent
        }
        install(ContentNegotiation) {
            json(Json)
        }
    }
}

fun URI.withPath(path: String) = URI(scheme, authority, path, query, fragment)

fun Long.formatFileSize(
    format: DecimalFormat = DecimalFormat("#.##")
): String {
    val kb = 1024.0
    val mb = kb * 1024
    val gb = mb * 1024

    return when {
        this >= gb -> "${format.format(this / gb)} GB"
        this >= mb -> "${format.format(this / mb)} MB"
        this >= kb -> "${format.format(this / kb)} KB"
        else -> "$this bytes"
    }
}