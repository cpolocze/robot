package com.msd.robot.domain

import com.msd.domain.ResourceType
import java.util.*
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class Inventory {
    @Id
    val id = UUID.randomUUID()
    var maxStorage = 20
        private set
    var usedStorage = 0
        private set

    @ElementCollection
    private val resourceMap = mutableMapOf(
        ResourceType.COAL to 0,
        ResourceType.IRON to 0,
        ResourceType.GEM to 0,
        ResourceType.GOLD to 0,
        ResourceType.PLATIN to 0,
    )

    /**
     * Adds a resource to this inventory. The inventory can hold all resources simultaneously, but the amount of
     * resources held cannot exceed <code>maxStorage</code>.
     *
     * @param resource  the resource which will be added to the inventory
     * @param amount    the amount that will be added
     */
    fun addResource(resource: ResourceType, amount: Int) {
        val newUsedStorage = usedStorage + amount
        if (newUsedStorage > maxStorage) {
            resourceMap[resource] = resourceMap[resource]!! + amount - (newUsedStorage - maxStorage)
            usedStorage = maxStorage
            throw InventoryFullException("Added resources exceed maxStorage. Would be ${newUsedStorage}, max is $maxStorage")
        } else {
            resourceMap[resource] = resourceMap[resource]!! + amount
            usedStorage += amount
        }
    }

    /**
     * Returns the stored amount of a given resource. The resource will still remain in the inventory
     *
     * @param resource  the resource of which the amount should be returned
     * @return          the stored amount of the resource as an <code>Int</code>
     */
    fun getStorageUsageForResource(resource: ResourceType): Int {
        return resourceMap[resource]!!
    }

    /**
     * Takes a specified amount of resources from this inventory. The resources will be removed from the inventory.
     *
     * @param resource  the resource which should be taken
     * @param amount    the amount which should be taken
     * @return          the taken amount of the resource
     */
    fun takeResource(resource: ResourceType, amount: Int): Boolean {
        if(resourceMap[resource]!! < amount) throw NotEnoughResourcesException("Wanted to take $amount, but only ${resourceMap[resource]!!} 10 were available")
        resourceMap[resource] = resourceMap[resource]!! - amount
        usedStorage -= amount
        return true
    }

    /**
     * Upgrades the <code>maxStorage</code> of this inventory.
     *
     * @param maxStorage the level to which the <code>maxStorage</code> should be upgraded to
     */
    internal fun upgrade(maxStorage: Int) {
        this.maxStorage = maxStorage
    }
}