package com.msd.robot.application

import com.msd.robot.domain.RobotRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityNotFoundException

@Service
class RobotApplicationService(
    val robotRepo: RobotRepository,
    val gameMapService: GameMapService
) {

    fun move(robotId: UUID, targetPlanetId: UUID, playerId: UUID) {
        val robot = robotRepo.findById(robotId).orElseThrow { throw EntityNotFoundException("No Robot with ID: $robotId") }

        if (robot.player == playerId)
            throw NotAllowedException(
                "Player $playerId is not allowed to move robot $robotId, because he is not " +
                    "the owner"
            )
        val planetDto = gameMapService.retrieveTargetPlanetIfRobotCanReach(robot.planet.id, targetPlanetId)
        val cost = planetDto.movementCost
        val planet = planetDto.toPlanet()
        robot.move(planet, cost)
    }
}
