package cafe.osrs.api.routes

import cafe.osrs.api.clients.ge.GEClient
import cafe.osrs.api.utils.ItemIdNotFoundException
import cafe.osrs.api.utils.setGEResponseHeaders
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.GrandExchangeRoute() {
    //TODO: add more search/filter routes
    get("/ge/mapping") {
        call.setGEResponseHeaders(GEClient.getGEClientCache())
        call.respond(GEClient.getMapping())
    }
    get("/ge/data") {
        call.setGEResponseHeaders(GEClient.getGEClientCache())
        call.respond(GEClient.getDataLatest())
    }
    get("/ge/items") {
        call.setGEResponseHeaders(GEClient.getGEClientCache())
        call.respond(GEClient.getItems())
    }
    get("/ge/icon/{id}") {
        val id = call.pathParameters["id"]?.toIntOrNull() ?: throw ItemIdNotFoundException()
        val detail = call.queryParameters.contains("detail").let { if(it) GEClient.IconType.DETAIL else GEClient.IconType.NORMAL }
        call.respondBytes(GEClient.getIcon(id, detail))
    }
}