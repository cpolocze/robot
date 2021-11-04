package com.msd.robot.application

import com.msd.robot.domain.NotEnoughEnergyException
import com.msd.robot.domain.PlanetBlockedException
import com.msd.robot.domain.RobotRepository
import org.springframework.stereotype.Service

@Service
class RobotApplicationService(
    val robotRepo: RobotRepository,
    val gameMapService: GameMapService
) {

    fun move(moveCommand: MovementCommand) {
        val robotId = moveCommand.robotId
        val playerId = moveCommand.playerUUID

        val robotOptional = robotRepo.findById(robotId)
        if (!robotOptional.isPresent) {
            // TODO throw failure Event
            return
        }
        val robot = robotOptional.get()

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
        } catch (ime: InvalidMoveException) {
            // TODO
            // throw failure Event
        } catch (cie: ClientInternalException) {
            // TODO
            // throw failure Event
        } catch (pbe: PlanetBlockedException) {
            // TODO
            // throw failure event
        } catch (noe: NotEnoughEnergyException) {
            // TODO
        }
        // TODO
        // throw succesfull execution event
    }
}
