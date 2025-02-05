package cafe.osrs.api.routes

import cafe.osrs.api.clients.hiscore.HiscoreClient
import cafe.osrs.api.clients.hiscore.HiscoreMode
import cafe.osrs.api.clients.hiscore.HiscoreResponseActivtityDTO
import cafe.osrs.api.clients.hiscore.HiscoreResponseSkillDTO
import cafe.osrs.api.utils.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class PlayerInfoResponse(
    val combatLevel: Double,
    val skills: List<HiscoreResponseSkillDTO>,
    val activities: List<HiscoreResponseActivtityDTO>
)

fun Route.PlayerRoute() {
    get("/player/{mode}/{name}") {
        val mode = HiscoreMode.get(call.pathParameters["mode"]) ?: throw BadHiscoreModeException()
        val name = call.pathParameters["name"].verifyValidCharacterName()
        val rankedOnly = call.queryParameters.contains("ranked")

        val response = HiscoreClient.getHiscore(mode = mode, player = name)
        call.setHiscoreResponseHeaders(response)

        when(response.status) {
            HttpStatusCode.OK -> {
                val data = response.response ?: throw GenericRSApiException(message = "Hiscore Response Status is OK but response is null")
                val info = PlayerInfoResponse(
                    combatLevel = CombatLevelCalculator.calculate(data),
                    skills = data.skills.filter { !rankedOnly || it.rank != -1 },
                    activities = data.activities.filter { !rankedOnly || it.rank != -1 }
                )
                call.respond(status = HttpStatusCode.OK, message = info)
            }
            HttpStatusCode.NotFound -> call.respondText(status = HttpStatusCode.NotFound, text = "")
            else -> {
                val errorId = String.random(10)
                println("[$errorId] Error!")
                println("[$errorId] Unexpected status: ${response.status}")
                println("[$errorId] Response: $response")
                throw GenericRSApiException(message = "Encountered an unexpected response status! Error id: $errorId")
            }
        }
    }
}