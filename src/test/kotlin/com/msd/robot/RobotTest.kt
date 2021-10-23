package com.msd.robot

import com.msd.robot.domain.UpgradeException
import com.msd.robot.domain.Robot
import com.msd.robot.domain.UpgradeType
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
    fun `Robot is dead after getting hit with more damage than he has HP`() {
        // when
        robot1.receiveDamage(12)

        // assert
        assert(robot1.health <= 0)
        assertFalse(robot1.alive)
    }

    @Test
    fun `Robot reduces own health by the amount of damage he got`() {
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
    fun `Robot causes damage on second robot according to its attackDamage when attacking`() {
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
    fun `Upgrading the level causes corresponding stat changes`() {
        // given
        robot1.upgrade(UpgradeType.DAMAGE)

        // when
        robot1.attack(robot2)

        // then
        assert(robot2.health == Robot.maxHealthByLevel[0] - Robot.attackDamageByLevel[1])
    }

    @Test
    fun `The damage level of a robot cannot go higher than 5`() {
        // given
        for (i in 1..5) robot1.upgrade(UpgradeType.DAMAGE)

        // assert
        assertThrows(UpgradeException::class.java){robot1.upgrade(UpgradeType.DAMAGE)}
    }

}