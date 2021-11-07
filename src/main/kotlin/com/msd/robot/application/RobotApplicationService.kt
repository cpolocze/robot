package com.msd.robot.application

import com.msd.application.GameMapService
import com.msd.command.BlockCommand
import com.msd.command.MovementCommand
import com.msd.robot.domain.Robot
import com.msd.robot.domain.RobotRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.*

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

        val robot = getRobot(robotId)

        checkPlayers(robot.player, playerId)
        val planetDto =
            gameMapService.retrieveTargetPlanetIfRobotCanReach(robot.planet.planetId, moveCommand.targetPlanetUUID)
        val cost = planetDto.movementCost
        val planet = planetDto.toPlanet()
        robot.move(planet, cost)
        robotRepo.save(robot)
    }

    /**
     * Makes the [Robot] specified in the [BlockCommand] block its current [Planet].
     *
     * @param blockCommand            The `BlockCommand` which specifies which robot should block
     * @throws RobotNotFoundException  if no robot with the ID specified in the `BlockCommand` can be found
     * @throws InvalidPlayerException  if the PlayerIDs specified in the `BlockCommand` and `Robot` don't match
     */
    fun block(blockCommand: BlockCommand) {
        val robot = getRobot(blockCommand.robotId)
        checkPlayers(robot.player, blockCommand.playerUUID)
        robot.block()
        robotRepo.save(robot)
    }

    /**
     * Checks if the two specified PlayerIDs match.
     *
     * @param robotPlayerId   the `playerId` of the [Robot]
     * @param commandPlayerId the `playerUUID` of the [Command]
     * @return `true` if the IDs match
     * @throws InvalidPlayerException if both `UUIDs` don't match
     */
    private fun checkPlayers(robotPlayerId: UUID, commandPlayerId: UUID) {
        if (robotPlayerId != commandPlayerId) throw InvalidPlayerException("Specified player doesn't match player specified in robot")
    }

    /**
     * Gets the specified [Robot].
     *
     * @param robotId the `UUID` of the robot which should be returned
     * @return the specified Robot
     * @throws RobotNotFoundException  if there is no `Robot` with the specified ID
     */
    private fun getRobot(robotId: UUID): Robot {
        return robotRepo.findByIdOrNull(robotId)
            ?: throw RobotNotFoundException("Can't find robot with id $robotId")
    }
}
