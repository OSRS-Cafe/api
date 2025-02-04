package cafe.osrs.api.routes

import cafe.osrs.api.clients.hiscore.HiscoreClient
import cafe.osrs.api.clients.hiscore.HiscoreMode
import cafe.osrs.api.utils.BadHiscoreModeException
import cafe.osrs.api.utils.setHiscoreResponseHeaders
import cafe.osrs.api.utils.verifyValidCharacterName
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class RankResponse(
    val skills: List<RankResponseSkill>,
    val activities: List<RankResponseActivity>
)

@Serializable
data class RankResponseSkill(
    val id: Int,
    val name: String,
    val rank: Int,
    val level: Int,
    val xp: Int
)

@Serializable
data class RankResponseActivity(
    val id: Int,
    val name: String,
    val rank: Int,
    val score: Int
)

fun Route.RankRoute() {
    get("/ranks/{mode}/{name}") {
        val mode = HiscoreMode.get(call.pathParameters["mode"]) ?: throw BadHiscoreModeException()
        val name = call.pathParameters["name"].verifyValidCharacterName()

        val hiscoreResponse = HiscoreClient.getHiscore(mode = mode, player = name)
        if(hiscoreResponse.status != HttpStatusCode.OK) return@get call.respondText(status = hiscoreResponse.status, text = hiscoreResponse.response ?: "")

        setHiscoreResponseHeaders(hiscoreResponse)

        val rankResponse = hiscoreResponse.response?.let { raw -> Json.decodeFromString<RankResponse>(raw) } ?: return@get call.respondText(status = HttpStatusCode.InternalServerError, text = "Response is empty!")

        call.respond(
            RankResponse(
                skills = rankResponse.skills.filter { it.rank != -1 },
                activities = rankResponse.activities.filter { it.rank != -1 }
            )
        )

    }
}