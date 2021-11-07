package com.msd.robot.application

import com.msd.application.GameMapService
import com.msd.command.BlockCommand
import com.msd.command.MovementCommand
import com.msd.robot.domain.RobotRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class RobotApplicationService(
    val robotRepo: RobotRepository,
    val gameMapService: GameMapService
) {

    /**
     * Executes a single [MovementCommand] by checking whether the robot exists and the player is the owner of the
     * robot. To get the new [Planet] the robot should be positioned on, if calls the GameMap MicroService through
     * a connector service [GameMapService]. If everything goes right, the robot gets moved.
     *
     * At this level of abstraction it should never throw an exception, but rather signal success or failure through
     * kafka events containing the transactionID of the command.
     */
    fun move(moveCommand: MovementCommand) {
        val robotId = moveCommand.robotId
        val playerId = moveCommand.playerUUID

        val robot =
            robotRepo.findByIdOrNull(robotId) ?: throw RobotNotFoundException("Can't find robot with id $robotId")

        if (robot.player != playerId) throw InvalidPlayerException("Specified player doesn't match player specified in robot")
        val planetDto =
            gameMapService.retrieveTargetPlanetIfRobotCanReach(robot.planet.planetId, moveCommand.targetPlanetUUID)
        val cost = planetDto.movementCost
        val planet = planetDto.toPlanet()
        robot.move(planet, cost)
        robotRepo.save(robot)
    }

    fun block(blockCommand: BlockCommand) {

    }
}
