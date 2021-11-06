package com.msd.robot.application

import com.msd.application.ClientException
import com.msd.application.GameMapService
import com.msd.command.MovementCommand
import com.msd.command.RegenCommand
import com.msd.robot.domain.NotEnoughEnergyException
import com.msd.robot.domain.PlanetBlockedException
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

        val robot = robotRepo.findByIdOrNull(robotId) ?: run {
            // TODO throw failure Event
            return
        }

        if (robot.player != playerId) {
            // TODO throw failure event
            return
        }
        try {
            val planetDto =
                gameMapService.retrieveTargetPlanetIfRobotCanReach(robot.planet.planetId, moveCommand.targetPlanetUUID)
            val cost = planetDto.movementCost
            val planet = planetDto.toPlanet()
            robot.move(planet, cost)
        } catch (ime: TargetPlanetNotReachableException) {
            // TODO
            // throw failure Event
        } catch (cie: ClientException) {
            // TODO
            // throw failure Event
        } catch (pbe: PlanetBlockedException) {
            // TODO
            // throw failure event
        } catch (noe: NotEnoughEnergyException) {
            // TODO
        }
        // TODO
        // throw successful execution event
    }

    fun regenerateEnergy(regenCommand: RegenCommand) {
    }
}
