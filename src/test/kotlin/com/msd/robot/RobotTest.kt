package com.msd.robot

import com.msd.robot.domain.UpgradeException
import com.msd.robot.domain.Robot
import com.msd.robot.domain.UpgradeType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.assertThrows
import java.util.*


class RobotTest {

    lateinit var robot1: Robot
    lateinit var robot2: Robot

    @BeforeEach
    fun initializeRobots() {
        robot1 = Robot(UUID.randomUUID())
        robot2 = Robot(UUID.randomUUID())
    }

    @Test()
    fun `Robot is dead after health reaches 0 or less`() {
        // when
        robot1.receiveDamage(12)
        robot2.receiveDamage(10)

        // assert
        assert(robot1.health <= 0)
        assertFalse(robot1.alive)
    }

    @Test
    fun `Robot health reduced by damage received`() {
        // when
        robot1.receiveDamage(9)

        // assert
        assert(robot1.health == 1)
        assert(robot1.alive)
    }

    @Test
    fun `Robot reduces energy when attacking`() {
        // when
        robot1.attack(robot2)

        // assert
        assert(robot1.energy == robot1.maxEnergy - robot1.damageLevel)
    }

    @Test
    fun `Robot causes damage to second robot according to its attackDamage when attacking`() {
        // when
        robot1.attack(robot2)

        // assert
        assert(robot2.health == Robot.maxHealthByLevel[0] - Robot.attackDamageByLevel[0])
    }

    //TODO
    @Test
    fun `Robot causes no damage if he doesnt have enough energy to attack`() {
        //TODO()
    }

    @Test
    fun `Upgrading a robot increases its upgrade level`(){
        //given
        for (upgradeType in UpgradeType.values()) robot1.upgrade(upgradeType)
        //then
        assertAll("All upgrade levels increase to 1",
            {
                assertEquals(1, robot1.storageLevel)
            },
            {
                assertEquals(1, robot1.healthLevel)
            },
            {
                assertEquals(1, robot1.damageLevel)
            },
            {
                assertEquals(1, robot1.miningSpeedLevel)
            },
            {
                assertEquals(1, robot1.miningLevel)
            },
            {
                assertEquals(1, robot1.energyLevel)
            },
            {
                assertEquals(1, robot1.energyRegenLevel)
            },
        )
        assertEquals(5, robot1.totalUpgrades())
    }

    @Test
    fun `Upgrading the level causes corresponding stat changes`() {
        // given
        for (upgradeType in UpgradeType.values()) robot1.upgrade(upgradeType)

        // then
        assertAll("",
            {
                assertEquals(Robot.storageByLevel[1], robot1.storageLevel)
            },
            {
                assertEquals(Robot.maxHealthByLevel[1], robot1.maxHealth)
            },
            {
                assertEquals(Robot.attackDamageByLevel[1], robot1.attackDamage)
            },
            {
                assertEquals(Robot.miningSpeedByLevel[1], robot1.miningSpeedLevel)
            },
            {
                assertEquals(Robot.miningByLevel[1], robot1.miningLevel)
            },
            {
                assertEquals(Robot.maxEnergyByLevel[1], robot1.maxEnergy)
            },
            {
                assertEquals(Robot.energyRegenByLevel[1], robot1.energyRegen)
            }
        )
    }

    @Test
    fun `The upgrade level of a robot cannot go higher than 5`() {
        // given
        for (upgradeType in UpgradeType.values()) for (i in 1..5) robot1.upgrade(upgradeType)
        // assert
        assertAll("Assert all upgrade levels being maxed at 5",
            {
                assertThrows<UpgradeException>("Max Storage Level has been reached. Upgrade not possible.") {
                    robot1.upgrade(UpgradeType.STORAGE)
                }
            },
            {
                assertThrows<UpgradeException>("Max Health Level has been reached. Upgrade not possible.") {
                    robot1.upgrade(UpgradeType.HEALTH)
                }
            },
            {
                assertThrows<UpgradeException>("Max Damage Level has been reached. Upgrade not possible.") {
                    robot1.upgrade(UpgradeType.DAMAGE)
                }
            },
            {
                assertThrows<UpgradeException>("Max Mining Speed has been reached. Upgrade not possible.") {
                    robot1.upgrade(UpgradeType.MINING_SPEED)
                }
            },
            {
                assertThrows<UpgradeException>("Max Mining Level has been reached. Upgrade not possible.") {
                    robot1.upgrade(UpgradeType.MINING)
                }
            },
            {
                assertThrows<UpgradeException>("Max Energy Level has been reached. Upgrade not possible.") {
                    robot1.upgrade(UpgradeType.MAX_ENERGY)
                }
            },
            {
                assertThrows<UpgradeException>("Max Energy Regen has been reached. Upgrade not possible.") {
                    robot1.upgrade(UpgradeType.ENERGY_REGEN)
                }
            })
    }

}