package cafe.osrs.api.clients.hiscore

enum class HiscoreMode(
    val endpoint: String
) {
    //NORMAL
    HISCORE_OLDSCHOOL(endpoint = "hiscore_oldschool"),
    MAIN(endpoint = HISCORE_OLDSCHOOL.endpoint),
    //IRONMAN
    HISCORE_OLDSCHOOL_IRONMAN(endpoint = "hiscore_oldschool_ironman"),
    IRONMAN(endpoint = HISCORE_OLDSCHOOL_IRONMAN.endpoint),
    HISCORE_OLDSCHOOL_ULTIMATE(endpoint = "hiscore_oldschool_ultimate"),
    ULTIMATE(endpoint = HISCORE_OLDSCHOOL_ULTIMATE.endpoint),
    HISCORE_OLDSCHOOL_HARDCORE_IRONMAN(endpoint = "hiscore_oldschool_hardcore_ironman"),
    HARDCORE_IRONMAN(endpoint = HISCORE_OLDSCHOOL_HARDCORE_IRONMAN.endpoint),
    //SEASONAL
    HISCORE_OLDSCHOOL_DEADMAN(endpoint = "hiscore_oldschool_deadman"),
    DEADMAN(endpoint = HISCORE_OLDSCHOOL_DEADMAN.endpoint),
    HISCORE_OLDSCHOOL_SEASONAL(endpoint = "hiscore_oldschool_seasonal"),
    SEASONAL(endpoint = HISCORE_OLDSCHOOL_SEASONAL.endpoint),
    HISCORE_OLDSCHOOL_TOURNAMENT(endpoint = "hiscore_oldschool_tournament"),
    TOURNAMENT(endpoint = HISCORE_OLDSCHOOL_TOURNAMENT.endpoint),
    //FRESH START
    HISCORE_OLDSCHOOL_FRESH_START(endpoint = "hiscore_oldschool_fresh_start"),
    FRESH_START(endpoint = HISCORE_OLDSCHOOL_FRESH_START.endpoint);

    companion object {
        fun get(name: String?) = entries.firstOrNull { it.name.equals(other = name, ignoreCase = true) }
        val prettyList = entries.joinToString(separator = ", ") { it.toString().lowercase() }
    }
}