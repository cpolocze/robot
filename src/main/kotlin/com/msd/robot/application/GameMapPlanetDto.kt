package com.msd.robot.application

import com.msd.domain.Planet
import com.msd.domain.PlanetType
import lombok.NoArgsConstructor
import java.util.*

@NoArgsConstructor
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
