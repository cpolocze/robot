package com.msd.robot.application

import com.msd.application.GameMapService
import com.msd.command.BlockCommand
import com.msd.command.MovementCommand
import com.msd.robot.domain.Robot
import com.msd.robot.domain.RobotDomainService
import org.springframework.stereotype.Service
import java.util.*

@Service
class RobotApplicationService(
    val gameMapService: GameMapService,
    val robotDomainService: RobotDomainService
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

        val robot = robotDomainService.getRobot(robotId)

        robotDomainService.doesRobotBelongsToPlayer(robot, playerId)
        val planetDto =
            gameMapService.retrieveTargetPlanetIfRobotCanReach(robot.planet.planetId, moveCommand.targetPlanetUUID)
        val cost = planetDto.movementCost
        val planet = planetDto.toPlanet()
        robot.move(planet, cost)
        robotDomainService.saveRobot(robot)
    }

    /**
     * Makes the [Robot] specified in the [BlockCommand] block its current [Planet].
     *
     * @param blockCommand            The `BlockCommand` which specifies which robot should block
     * @throws RobotNotFoundException  if no robot with the ID specified in the `BlockCommand` can be found
     * @throws InvalidPlayerException  if the PlayerIDs specified in the `BlockCommand` and `Robot` don't match
     */
    fun block(blockCommand: BlockCommand) {
        val robot = robotDomainService.getRobot(blockCommand.robotId)
        robotDomainService.doesRobotBelongsToPlayer(robot, blockCommand.playerUUID)
        robot.block()
        robotDomainService.saveRobot(robot)
    }
}
