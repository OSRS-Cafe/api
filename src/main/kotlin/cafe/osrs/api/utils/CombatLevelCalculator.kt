package cafe.osrs.api.utils

import cafe.osrs.api.clients.hiscore.HiscoreResponseDTO

object CombatLevelCalculator {
    fun calculate(
        attack: Int,
        strength: Int,
        defence: Int,
        hitpoints: Int,
        magic: Int,
        ranged: Int,
        prayer: Int
    ): Double {
        val base = (defence + hitpoints + (prayer / 2)) / 4.0
        val melee = (attack + strength) * 1.3
        val mage = magic * 2.0 * 1.3
        val range = ranged * 2.0 * 1.3

        val maxCombatStyle = maxOf(melee, mage, range)

        return base + maxCombatStyle / 4.0
    }

    fun calculate(hiscoreResponse: HiscoreResponseDTO): Double {
        fun skill(name: String) = hiscoreResponse.skills.first { it.name == name }.level

        return calculate(
            attack = skill("Attack"),
            defence = skill("Defence"),
            strength = skill("Strength"),
            hitpoints = skill("Hitpoints"),
            prayer = skill("Prayer"),
            magic = skill("Magic"),
            ranged = skill("Ranged"),
        )
    }
}