package cafe.osrs.api.routes

import cafe.osrs.api.CafeAPI
import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.SwaggerRoute() {
    val swaggerStatic = CafeAPI::class.java.getResourceAsStream("/swagger.html").use { it!!.readAllBytes() }
    get("/") {
        call.respondBytes(status = HttpStatusCode.OK, contentType = ContentType.Text.Html, bytes = swaggerStatic)
    }
}