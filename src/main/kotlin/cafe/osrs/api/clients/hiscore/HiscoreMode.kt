package cafe.osrs.api.clients.hiscore

enum class HiscoreMode(
    val endpoint: String
) {
    //NORMAL
    HISCORE_OLDSCHOOL(endpoint = "hiscore_oldschool"),
    //IRONMAN
    HISCORE_OLDSCHOOL_IRONMAN(endpoint = "hiscore_oldschool_ironman"),
    HISCORE_OLDSCHOOL_ULTIMATE(endpoint = "hiscore_oldschool_ultimate"),
    HISCORE_OLDSCHOOL_HARDCORE_IRONMAN(endpoint = "hiscore_oldschool_hardcore_ironman"),
    //SEASONAL
    HISCORE_OLDSCHOOL_DEADMAN(endpoint = "hiscore_oldschool_deadman"),
    HISCORE_OLDSCHOOL_SEASONAL(endpoint = "hiscore_oldschool_seasonal"),
    HISCORE_OLDSCHOOL_TOURNAMENT(endpoint = "hiscore_oldschool_tournament"),
    //FRESH START
    HISCORE_OLDSCHOOL_FRESH_START(endpoint = "hiscore_oldschool_fresh_start");

    companion object {
        fun get(name: String?) = entries.firstOrNull { it.name.equals(other = name, ignoreCase = true) }
        val prettyList = entries.joinToString(separator = ", ") { it.toString().lowercase() }
    }
}