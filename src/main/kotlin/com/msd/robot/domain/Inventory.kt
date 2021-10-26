package com.msd.robot.domain

import com.msd.domain.Resource
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
    private val resourceMap = mapOf(
        Pair(Resource.COAL, 0),
        Pair(Resource.IRON, 0),
        Pair(Resource.GEM, 0),
        Pair(Resource.GOLD, 0),
        Pair(Resource.PLATINUM, 0),
    )

    fun addResource(resource: Resource, amount: Int) {
        TODO()
    }

    fun getStorageUsageForResource(resource: Resource): Int {
        TODO()
    }

    fun takeResource(resource: Resource, amount: Int): Pair<Resource, Int> {
        TODO()
    }

    internal fun upgrade() {
        TODO()
    }
}