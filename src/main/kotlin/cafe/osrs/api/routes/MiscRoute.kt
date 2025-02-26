package cafe.osrs.api.routes

import cafe.osrs.api.clients.playercount.PlayerCountClient
import cafe.osrs.api.utils.BadCombatLevelRequestException
import cafe.osrs.api.utils.CombatLevelCalculator
import cafe.osrs.api.utils.CombatLevelStats
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.MiscRoute() {
    route("/misc/") {
        get("/combatLevel") {
            val stats = kotlin.runCatching { call.receiveNullable<CombatLevelStats>() }.getOrNull() ?: throw BadCombatLevelRequestException()
            call.respond(CombatLevelCalculator.calculate(stats))
        }
        get("/playerCount") {
            call.respond(PlayerCountClient.getPlayerCount())
        }
    }
}