package cafe.osrs.api.routes

import cafe.osrs.api.clients.hiscore.HiscoreClient
import cafe.osrs.api.clients.hiscore.HiscoreMode
import cafe.osrs.api.utils.*
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.PlayerRoute() {
    get("/player/{mode}/{name}") {
        val mode = HiscoreMode.get(call.pathParameters["mode"]) ?: throw BadHiscoreModeException()
        val name = call.pathParameters["name"].verifyValidCharacterName()

        val response = HiscoreClient.getHiscore(mode = mode, player = name)
        setHiscoreResponseHeaders(response)

        when(response.status) {
            HttpStatusCode.OK, HttpStatusCode.NotFound -> call.respondText(status = response.status, text = response.response ?: "")
            else -> {
                val errorId = String.random(10)
                println("[$errorId] Error!")
                println("[$errorId] Unexpected status: ${response.status}")
                println("[$errorId] Response: $response")
                call.respondText(status = HttpStatusCode.InternalServerError, text = "Internal Server Error. Error id: $errorId")
            }
        }
    }
}