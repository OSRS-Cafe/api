package cafe.osrs.api.routes

import cafe.osrs.api.clients.worlds.*
import cafe.osrs.api.utils.BadOptionException
import cafe.osrs.api.utils.GenericUserErrorException
import cafe.osrs.api.utils.all
import io.ktor.http.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import io.ktor.server.response.*
import io.ktor.util.logging.*
import kotlin.math.truncate

@Serializable
data class WorldsResponse(
    val players: Int,
    val filteredPlayers: Int,
    val worlds: List<WorldInfo>
)

fun Route.WorldsRoute() {
    //TODO: Don't try to map blank query parameters (eg: activity=) to an enum, instead treat it as not set?
    get("/worlds") {
        val nameFilter = call.queryParameters["name"] ?: ""
        val accessFilter = call.queryParameters["access"]?.let { WorldAccess.get(it.uppercase()) }
        val locationFilter = call.queryParameters["location"]?.split(",")?.map { WorldLocation.get(it) }.orEmpty().take(WorldLocation.entries.size)
        val activityFilter = call.queryParameters["activity"]?.split(",")?.map { WorldActivity.get(it) }.orEmpty().take(WorldActivity.entries.size)
        val playersFilter: List<(Int) -> (Boolean)> = call.queryParameters["players"]?.split(",")?.map {
            val filter: ((Int) -> (Boolean)) = { players ->
                try {
                    when {
                        it.startsWith(">=") -> players >= (it.removePrefix(">=").toInt())
                        it.startsWith("<=") -> players <= (it.removePrefix("<=").toInt())
                        it.startsWith("<") -> players < (it.removePrefix("<").toInt())
                        it.startsWith(">") -> players > (it.removePrefix(">").toInt())
                        else -> throw GenericUserErrorException("Bad expression. Examples: >500,<500,>=500,<=500.")
                    }
                } catch (e: NumberFormatException) {
                    throw GenericUserErrorException("Bad expression. Examples: >500,<500,>=500,<=500.")
                }
            }
            filter
        }.orEmpty().take(5) //Limit size of filters

        val info = WorldsClient.worldsStore.get()

        val filteredWorlds = info.worlds.filter { world ->
            all(
                nameFilter.isBlank() || world.name.contains(nameFilter),
                accessFilter == null || world.access == accessFilter,
                locationFilter.isEmpty() || locationFilter.contains(world.location),
                activityFilter.isEmpty() || activityFilter.contains(world.activity),
                playersFilter.isEmpty() || playersFilter.all { filter -> filter.invoke(world.players) }
            )
        }

        val response = WorldsResponse(
            players = info.players,
            filteredPlayers = filteredWorlds.sumOf { it.players },
            worlds = filteredWorlds
        )
        call.respond(response)
    }
}