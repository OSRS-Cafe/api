package cafe.osrs.api.clients.worlds

import kotlinx.serialization.Serializable

@Serializable
data class WorldInfo(
    val name: String,
    val id: Int,
    val players: Int,
    val location: WorldLocation,
    val access: WorldAccess,
    val activity: WorldActivity
)

data class WorldsInfo(
    val players: Int,
    val worlds: List<WorldInfo>
)

object WorldsParser {
    private data class RawWorldInfo(
        val name: String,
        val players: String,
        val location: String,
        val access: String,
        val activity: String
    )

    private object RegexPresets {
        val playerCount = "<p class='player-count'>There are currently (.*) people playing!</p>".toRegex()
        val serverRow = "<tr class='server-list__row(.*?)</tr>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val serverData = "<td class='server-list__row-cell(.*?)</td>".toRegex(RegexOption.DOT_MATCHES_ALL)
        val serverDataValue = ">(.*?)<".toRegex()
        val serverId = "id='slu-world-(.*?)'".toRegex(RegexOption.DOT_MATCHES_ALL)
    }

    fun parse(html: String): WorldsInfo {
        fun getParam(regex: Regex, input: String) = regex.findAll(input).first().groupValues[1]

        val playerCount = getParam(RegexPresets.playerCount, html).replace(",", "").toInt()

        val serverInfoItems = RegexPresets.serverRow.findAll(html).toList().map { it.value }.map { item ->
            val parts = RegexPresets.serverData.findAll(item).toList().map { it.value }
            RawWorldInfo(name = parts[0], players = parts[1], location = parts[2], access = parts[3], activity = parts[4])
        }.map {
            fun p(input: String) = getParam(RegexPresets.serverDataValue, input)
            WorldInfo(
                name = p(it.name),
                id = getParam(RegexPresets.serverId, it.name).toInt(),
                players = p(it.players).removeSuffix(suffix = " players").toIntOrNull() ?: -1,
                location = WorldLocation.fromListName(p(it.location)),
                access = WorldAccess.fromListName(p(it.access)),
                activity = WorldActivity.fromListName(p(it.activity))
            )
        }
        return WorldsInfo(players = playerCount, worlds = serverInfoItems)
    }
}