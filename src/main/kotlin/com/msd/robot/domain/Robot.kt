package com.msd.robot.domain

import com.msd.domain.Planet
import com.msd.domain.ResourceType
import java.lang.IllegalArgumentException
import java.lang.RuntimeException
import java.util.*
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToOne

@Entity
class Robot(
    val player: UUID,
    @Embedded var planet: Planet
) {
    @Id
    val id: UUID = UUID.randomUUID()


    var alive: Boolean = true

    val maxHealth
        get() = maxHealthByLevel[healthLevel]

    val maxEnergy
        get() = maxEnergyByLevel[energyLevel]

    val energyRegen
        get() = energyRegenByLevel[energyRegenLevel]

    val attackDamage: Int
        get() = attackDamageByLevel[damageLevel]

    @OneToOne
    val inventory = Inventory()
    val miningSpeed: Int
        get() = miningSpeedByLevel[miningSpeedLevel]

    var health: Int = maxHealth
        private set

    var energy: Int = maxEnergy
        private set

    var storageLevel: Int = 0
        private set(value) {
            if (value > 5) throw UpgradeException("Max Storage Level has been reached. Upgrade not possible.")
            else if (value > storageLevel + 1)
                throw UpgradeException(
                    "Cannot skip upgrade levels. Tried to upgrade from level $storageLevel to level $value"
                )
            else if (value <= storageLevel)
                throw UpgradeException("Cannot downgrade Robot. Tried to go from level $storageLevel to level $value")
            field = value
        }

    var healthLevel: Int = 0
        private set(value) {
            if (value > 5) throw UpgradeException("Max Health Level has been reached. Upgrade not possible.")
            else if (value > healthLevel + 1)
                throw UpgradeException(
                    "Cannot skip upgrade levels. Tried to upgrade from level $healthLevel to level $value"
                )
            else if (value <= healthLevel)
                throw UpgradeException("Cannot downgrade Robot. Tried to go from level $healthLevel to level $value")
            field = value
        }

    var damageLevel: Int = 0
        private set(value) {
            if (value > 5) throw UpgradeException("Max Damage Level has been reached. Upgrade not possible.")
            else if (value > damageLevel + 1)
                throw UpgradeException(
                    "Cannot skip upgrade levels. " +
                            "Tried to upgrade from level $damageLevel to level $value"
                )
            else if (value <= damageLevel)
                throw UpgradeException("Cannot downgrade Robot. Tried to go from level $damageLevel to level $value")
            field = value
        }

    var miningSpeedLevel: Int = 0
        private set(value) {
            if (value > 5) throw UpgradeException("Max MiningSpeed Level has been reached. Upgrade not possible.")
            else if (value > miningSpeedLevel + 1)
                throw UpgradeException(
                    "Cannot skip upgrade levels. Tried to upgrade from level $miningSpeedLevel to level $value"
                )
            else if (value <= miningSpeedLevel)
                throw UpgradeException("Cannot downgrade Robot. Tried to go from level $miningSpeedLevel to level $value")
            field = value
        }

    var miningLevel: Int = 0
        private set(value) {
            if (value > 4) throw UpgradeException("Max Mining Level has been reached. Upgrade not possible.")
            else if (value > miningLevel + 1)
                throw UpgradeException(
                    "Cannot skip upgrade levels. Tried to upgrade from level $miningLevel to level $value"
                )
            else if (value <= miningLevel)
                throw UpgradeException("Cannot downgrade Robot. Tried to go from level $miningLevel to level $value")
            field = value
        }

    var energyLevel: Int = 0
        private set(value) {
            if (value > 5) throw UpgradeException("Max Energy Level has been reached. Upgrade not possible.")
            else if (value > energyLevel + 1)
                throw UpgradeException(
                    "Cannot skip upgrade levels. Tried to upgrade from level $energyLevel to level $value"
                )
            else if (value <= energyLevel)
                throw UpgradeException("Cannot downgrade Robot. Tried to go from level $energyLevel to level $value")
            field = value
        }

    var energyRegenLevel: Int = 0
        private set(value) {
            if (value > 5) throw UpgradeException("Max Energy Regen Level has been reached. Upgrade not possible.")
            else if (value > energyRegenLevel + 1)
                throw UpgradeException(
                    "Cannot skip upgrade levels. Tried to upgrade from level $energyRegenLevel to level $value"
                )
            else if (value <= energyRegenLevel)
                throw UpgradeException("Cannot downgrade Robot. Tried to go from level $energyRegenLevel to level $value")
            field = value
        }

    fun move(planet: Planet, cost: Int) {
        this.planet = planet
        this.reduceEnergy(cost)
    }

    fun block() {
        TODO()
    }

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
        } catch (re: RuntimeException) {
            /*
            TODO
            This gets thrown if the robot can not attack for some reason, currently that's fine and we don't
            want to handle this. We just act like the robot didn't try to attack. We probably wanna throw an event
            here in the future, to notify the player who tried to attack.
            */
        }
    }

    fun upgrade(upgradeType: UpgradeType) {
        when (upgradeType) {
            UpgradeType.DAMAGE -> damageLevel++
            UpgradeType.ENERGY_REGEN -> energyRegenLevel++
            UpgradeType.MAX_ENERGY -> energyLevel++
            UpgradeType.HEALTH -> healthLevel++
            UpgradeType.STORAGE -> {
                storageLevel++
                inventory.upgrade(storageByLevel[storageLevel])
            }
            UpgradeType.MINING_SPEED -> miningSpeedLevel++
            UpgradeType.MINING -> miningLevel++
        }

    }

    fun canMine(resourceType: ResourceType): Boolean {
        return this.miningLevel >= resourceType.requiredMiningLevel
    }

    fun totalUpgrades(): Int {
        return damageLevel + energyLevel + energyRegenLevel + healthLevel + storageLevel + miningSpeedLevel + miningLevel
    }

    companion object {
        val storageByLevel = arrayOf(20, 50, 100, 200, 400, 1000)
        val maxHealthByLevel = arrayOf(10, 25, 50, 100, 200, 500)
        val attackDamageByLevel = arrayOf(1, 2, 5, 10, 20, 50)
        val miningSpeedByLevel = arrayOf(2, 5, 10, 15, 20, 40)
        val maxEnergyByLevel = arrayOf(20, 30, 40, 60, 100, 200)
        val energyRegenByLevel = arrayOf(4, 6, 8, 10, 15, 20)
    }
}