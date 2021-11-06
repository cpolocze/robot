package com.msd.application

import com.msd.planet.domain.Planet
import com.msd.planet.domain.PlanetType
import java.util.*

class GameMapPlanetDto(
    val id: UUID,
    val movementCost: Int,
    val type: PlanetType,
    val playerId: UUID?
) {

    fun toPlanet(): Planet {
        return Planet(id, type, playerId)
    }
}
