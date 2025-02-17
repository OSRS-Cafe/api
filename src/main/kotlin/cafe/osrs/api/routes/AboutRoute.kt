package cafe.osrs.api.routes

import cafe.osrs.api.utils.BuildInfo
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.AboutRoute() {
    get("/about") {
        call.respond(BuildInfo.info)
    }
}