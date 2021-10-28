package com.msd.domain

import java.util.*
import javax.persistence.Embeddable
import javax.persistence.Id

@Embeddable
class Planet(@Id val id: UUID, val type: PlanetType = PlanetType.STANDARD, val playerId: UUID? = null) {

    var blocked: Boolean = false

    init {
        if(type == PlanetType.SPAWN && playerId == null) throw NullPointerException("Spawns must have a playerId")
        if(type != PlanetType.SPAWN && playerId != null) throw IllegalArgumentException("Only spawns can have a playerId")
    }
}