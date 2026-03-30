package cafe.osrs.api.clients.worlds

import cafe.osrs.api.utils.BadOptionException

enum class WorldActivity(private val listName: String) {
    UNKNOWN("?"),
    NONE("-"),
    SKILL_TOTAL_500("500 skill total"),
    SKILL_TOTAL_750("750 skill total"),
    SKILL_TOTAL_1250("1250 skill total"),
    SKILL_TOTAL_1500("1500 skill total"),
    SKILL_TOTAL_1750("1750 skill total"),
    SKILL_TOTAL_2000("2000 skill total"),
    SKILL_TOTAL_2200("2200 skill total"),
    SKILL_TOTAL_2350("2350 skill total"),
    AGILITY_TRAINING("Agility Training"),
    BARBARIAN_ASSAULT("Barbarian Assault"),
    BLAST_FURNACE("Blast Furnace"),
    LMS_CASUAL("LMS Casual"),
    PVP_ARENA_LEGACY_DUELS("PvP Arena (Legacy Duels)"),
    TRADE_FREE("Trade - Free"),
    FORESTRY("Forestry"),
    CASTLE_WARS_FREE("Castle Wars - Free"),
    FRESH_START("Fresh Start"),
    PVP_WORLD_FREE("PvP World - Free"),
    CLAN_RECRUITMENT("Clan Recruitment"),
    WILDERNESS_PK_FREE("Wilderness PK - Free"),
    SPEEDRUNNING_WORLD("Speedrunning World"),
    PVP_ARENA_US("PvP Arena (US)"),
    DEADMAN("Deadman"),
    PVP_WORLD_HIGH_RISK("PvP World - High Risk"),
    HIGH_RISK_WORLD("High Risk World"),
    NEX_FFA("Nex FFA"),
    BRIMHAVEN_AGILITY("Brimhaven Agility"),
    BRIMHAVEN_AGILITY_ARENA("Brimhaven Agility Arena"),
    CASTLE_WARS_2("Castle Wars 2"),
    CLAN_WARS_FREE_FOR_ALL("Clan Wars - Free-for-all"),
    ROYAL_TITANS("Royal Titans"),
    PYRAMID_PLUNDER("Pyramid Plunder"),
    NIGHTMARE_OF_ASHIHAMA("Nightmare of Ashihama"),
    BURTHORPE_GAMES_ROOM("Burthorpe Games Room"),
    TZHAAR_FIGHT_PIT("TzHaar Fight Pit"),
    WILDERNESS_PK_MEMBERS("Wilderness PK - Members"),
    SULLIUSCEP_CUTTING("Sulliuscep cutting"),
    FISHING_TRAWLER("Fishing Trawler"),
    FALADOR_PARTY_ROOM("Falador Party Room"),
    GUARDIANS_OF_THE_RIFT("Guardians of the Rift"),
    OURANIA_ALTAR("Ourania Altar"),
    MORT_TON_TEMPLE_RAT_PITS("Mort'ton temple, Rat Pits"),
    TOMBS_OF_AMASCUT("Tombs of Amascut"),
    LMS_COMPETITIVE("LMS Competitive"),
    TOA_FFA("ToA FFA"),
    TEMPOROSS("Tempoross"),
    ZALCANO("Zalcano"),
    THEATRE_OF_BLOOD("Theatre of Blood"),
    GROUP_SKILLING("Group Skilling"),
    TROUBLE_BREWING("Trouble Brewing"),
    WINTERTODT("Wintertodt"),
    HOUSE_PARTY_GILDED_ALTAR("House Party, Gilded Altar"),
    PEST_CONTROL("Pest Control"),
    SOUL_WARS("Soul Wars"),
    BOUNTY_HUNTER_WORLD("Bounty Hunter World"),
    VOLCANIC_MINE("Volcanic Mine"),
    VARLAMORE_PVM("Varlamore PvM"),
    PVP_WORLD("PvP World"),
    PVP_ARENA_UK("PvP Arena (UK)"),
    PVP_ARENA_AUS("PvP Arena (AUS)"),
    GROUP_PVM("Group PvM"),
    TRADE__MEMBERS("Trade - Members"),
    ROLEPLAYING("Role-playing"),
    ZEAH_RUNECRAFTING("Zeah Runecrafting"),
    CASTLE_WARS_1("Castle Wars 1"),
    SALVAGING("Salvaging"),
    YAMA("Yama"),
    DEADMAN__PERMANENT("Deadman - Permanent"),
    SORCERESS_S_GARDEN("Sorceress's Garden"),
    MASTERING_MIXOLOGY("Mastering Mixology");

    companion object {
        fun get(name: String) = WorldActivity.getOrNull(name) ?: throw BadOptionException(name, entries.map { it.name })
        fun getOrNull(name: String) = entries.firstOrNull { it.name.equals(other = name, ignoreCase = true) }
        fun fromListName(listName: String) = entries.firstOrNull { it.listName == listName } ?: run {
            val recommendedEnumName = listName.uppercase()
                .replace(" ", "_")
                .replace("-", "")
                .replace(",", "")
                .replace("'", "_")
                .replace("(", "").replace(")", "")
            println("List name not found: $listName! Recommended name: $recommendedEnumName(\"$listName\")")
            UNKNOWN
        }
    }
}