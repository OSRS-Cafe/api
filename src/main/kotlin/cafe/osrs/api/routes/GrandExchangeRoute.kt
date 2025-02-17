package cafe.osrs.api.routes

import cafe.osrs.api.clients.ge.GEClient
import cafe.osrs.api.utils.ComputedStore
import cafe.osrs.api.utils.ItemIdNotFoundException
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.GrandExchangeRoute() {
    fun setHeaders(call: RoutingCall, store: ComputedStore<*>) {
        call.response.header("Cache-Last-Refresh", store.lastRefresh)
        call.response.header("Cache-Next-Refresh", store.nextRefresh)
    }

    get("/ge/raw/mapping") {
        val mappingStore = GEClient.mappingStore
        val mapping = mappingStore.get()
        setHeaders(call, mappingStore)
        call.respond(mapping)
    }

    get("/ge/raw/volumes") {
        val volumesStore = GEClient.volumesStore
        val volumes = volumesStore.get()
        setHeaders(call, volumesStore)
        call.respond(volumes)
    }

    get("/ge/raw/data") {
        val dataStore = GEClient.dataStore
        val data = dataStore.get()
        setHeaders(call, dataStore)
        call.respond(data)
    }

    get("/ge/items") {
        val itemStore = GEClient.itemStore
        val items = itemStore.get()
        setHeaders(call, itemStore)
        call.respond(items)
    }

    get("/ge/icon/{id}") {
        val id = call.pathParameters["id"]?.toIntOrNull() ?: throw ItemIdNotFoundException()
        val detail = call.queryParameters.contains("detail").let { if(it) GEClient.IconType.DETAIL else GEClient.IconType.NORMAL }
        call.respondBytes(GEClient.getIcon(id, detail))
    }

    /**
     *  TODO: Add more search filter possibilities
     *  cabbage <- contains
     *  =cabbage <- exact
     *  !cabbage <- doesnt contain
     *  =19 <19 >19 25><50 or with a = between?
     */

    get("/ge/search") {
        val itemStore = GEClient.itemStore
        val items = itemStore.get()
        setHeaders(call, itemStore)

        val name = call.queryParameters["name"]
        val examine = call.queryParameters["examine"]
        val id = call.queryParameters["id"]
        val members = call.queryParameters["members"]

        val fItems = items
            .filter { name == null || it.name.contains(other = name, ignoreCase = true) }
            .filter { examine == null || it.examine.contains(other = examine, ignoreCase = true) }
            .filter { id == null || it.id.toString().contains(other = id, ignoreCase = true) }
            .filter { members == null || it.members == members.toBooleanStrictOrNull() }
        call.respond(fItems)
    }
}