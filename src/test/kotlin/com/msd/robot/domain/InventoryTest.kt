package com.msd.robot.domain

import com.msd.domain.Resource
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class InventoryTest {

    private lateinit var robot1: Robot

    @BeforeEach
    fun initialize() {
        robot1 = Robot(UUID.randomUUID())
    }

    @Test
    fun `Used storage can't exceed max storage`() {
        //when
        robot1.inventory.addResource(Resource.COAL, 25)
        //then
        assertEquals(20, robot1.inventory.usedStorage)
        assertEquals(20, robot1.inventory.getStorageUsageForResource(Resource.COAL))
    }

    @Test
    fun `Used storage is correct when holding different resources`() {
        //when
        robot1.inventory.addResource(Resource.COAL, 5)
        robot1.inventory.addResource(Resource.IRON, 10)
        robot1.inventory.addResource(Resource.PLATINUM, 5)
        //then
        assertEquals(20, robot1.inventory.usedStorage)
    }

    @Test
    fun `Inventory can hold multiple resource`() {
        //when
        robot1.inventory.addResource(Resource.COAL, 5)
        robot1.inventory.addResource(Resource.IRON, 10)
        robot1.inventory.addResource(Resource.PLATINUM, 5)
        //then
        assertAll(
            {
                assertEquals(5, robot1.inventory.getStorageUsageForResource(Resource.COAL))
            },
            {
                assertEquals(10, robot1.inventory.getStorageUsageForResource(Resource.IRON))
            },
            {
                assertEquals(5, robot1.inventory.getStorageUsageForResource(Resource.PLATINUM))
            }
        )
    }

    @Test
    fun `Removing resource correctly reduces used storage`() {
        //given
        robot1.inventory.addResource(Resource.COAL, 5)
        robot1.inventory.addResource(Resource.IRON, 10)
        robot1.inventory.addResource(Resource.PLATINUM, 5)

        //when
        robot1.inventory.takeResource(Resource.COAL, 3)
        robot1.inventory.takeResource(Resource.IRON, 1)
        robot1.inventory.takeResource(Resource.PLATINUM, 4)

        //then
        assertEquals(12, robot1.inventory.usedStorage)
        assertAll(
            {
                assertEquals(2, robot1.inventory.getStorageUsageForResource(Resource.COAL))
            },
            {
                assertEquals(4, robot1.inventory.getStorageUsageForResource(Resource.IRON))
            },
            {
                assertEquals(6, robot1.inventory.getStorageUsageForResource(Resource.PLATINUM))
            }
        )
    }

    @Test
    fun `Removing resources from storage can only remove available resources`() {
        //given
        robot1.inventory.addResource(Resource.IRON, 10)
        //when
        val resourcePair = robot1.inventory.takeResource(Resource.IRON, 15)
        //then
        assertEquals(Pair(Resource.IRON, 10), resourcePair)
        assertEquals(0, robot1.inventory.usedStorage)
    }

    @Test
    fun `Upgrading storage allows robot to hold more resources`() {
        //given
        robot1.upgrade(UpgradeType.STORAGE)
        //when
        robot1.inventory.addResource(Resource.IRON, 25)
        //then
        assertEquals(50, robot1.inventory.maxStorage)
        assertEquals(25, robot1.inventory.getStorageUsageForResource(Resource.IRON))
    }
}