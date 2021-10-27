package com.msd.domain

import java.util.*

class Planet(val id: UUID, val type: PlanetType, val playerId: UUID? = null) {

    init {
        if(type == PlanetType.SPAWN && playerId == null) throw NullPointerException("Spawns must have a playerId")
        if(type != PlanetType.SPAWN && playerId != null) throw IllegalArgumentException("Only spawns can have a playerId")
    }
}