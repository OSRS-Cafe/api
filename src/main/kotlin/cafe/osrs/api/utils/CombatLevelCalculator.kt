package cafe.osrs.api.utils

import cafe.osrs.api.clients.hiscore.HiscoreResponseDTO
import kotlinx.serialization.Serializable

@Serializable
data class CombatLevelStats(
    val attack: Int,
    val strength: Int,
    val defence: Int,
    val hitpoints: Int,
    val magic: Int,
    val ranged: Int,
    val prayer: Int
)

object CombatLevelCalculator {
    fun calculate(stats: CombatLevelStats): Double {
        val base = (stats.defence + stats.hitpoints + (stats.prayer / 2)) / 4.0
        val melee = (stats.attack + stats.strength) * 1.3
        val mage = stats.magic * 2.0 * 1.3
        val range = stats.ranged * 2.0 * 1.3

        val maxCombatStyle = maxOf(melee, mage, range)

        return base + maxCombatStyle / 4.0
    }

    fun calculate(hiscoreResponse: HiscoreResponseDTO): Double {
        fun skill(name: String) = hiscoreResponse.skills.first { it.name == name }.level

        return calculate(CombatLevelStats(
            attack = skill("Attack"),
            defence = skill("Defence"),
            strength = skill("Strength"),
            hitpoints = skill("Hitpoints"),
            prayer = skill("Prayer"),
            magic = skill("Magic"),
            ranged = skill("Ranged")
        ))
    }
}