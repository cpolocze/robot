package com.msd.robot.domain

import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Robot(
        val player: UUID,
) {
    @Id
    val id: UUID = UUID.randomUUID()

    var maxHealth: Int = maxHealthByLevel[0]
        private set

    var health: Int = maxHealth
        private set

    var alive: Boolean = true

    val maxEnergy
        get() = maxEnergyByLevel[energyLevel]

    var energy: Int = maxEnergy
        private set

    val attackDamage: Int
        get() = attackDamageByLevel[damageLevel]


    var storageLevel: Int = 0
        private set

    var healthLevel: Int = 0
        private set

    var damageLevel: Int = 0
        private set(value) {
            if (value > 5) throw UpgradeException("Max Damage Level has been reached. Upgrade not possible.")
            else if (value > damageLevel + 1)
                throw UpgradeException("Cannot skip upgrade levels. " +
                        "Tried to upgrade from level $damageLevel to level $value")
            else if (value <= damageLevel)
                throw UpgradeException("Cannot downgrade Robot. Tried to go from level $damageLevel to level $value")
            field = value
        }
    var miningSpeedLevel: Int = 0
        private set
    var resourceMiningLevel: Int = 0
        private set
    var energyLevel: Int = 0
        private set
    var energyRegenLevel: Int = 0
        private set


    fun receiveDamage(damage: Int) {
        this.health -= damage
        if (health <= 0) alive = false
    }

    private fun reduceEnergy(amount: Int) {
        if (amount > energy) throw NotEnoughEnergyException("Tried to reduce energy by $amount but only has $energy energy")
        if (amount < 0) throw IllegalArgumentException("Used energy amount cannot be less than zero")

        energy -= amount
    }

    fun attack(otherRobot: Robot) {
        try {
            reduceEnergy(damageLevel)
            otherRobot.receiveDamage(attackDamage)
        } catch (neee: RuntimeException) {

        }
    }

    fun upgrade(upgradeType: UpgradeType) {
        when (upgradeType) {
            UpgradeType.DAMAGE -> damageLevel++
        }

    }


    companion object {
        val storageByLevel = arrayOf(20, 50, 100, 200, 400, 1000)
        val maxHealthByLevel = arrayOf(10, 25, 50, 100, 200, 500)
        val attackDamageByLevel = arrayOf(1, 2, 5, 10, 20, 50)
        val miningSpeedByLevel = arrayOf(2, 5, 10, 15, 20, 40)
        // TODO
        // val resourceMining
        val maxEnergyByLevel = arrayOf(20, 30, 40, 60, 100, 200)
        val energyRegenByLevel = arrayOf(4, 6, 8, 10, 15, 20)


    }
}