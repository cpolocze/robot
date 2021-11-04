package com.msd.robot.application

import java.util.*

class MovementCommand(
    val playerUUID: UUID,
    robotUUID: UUID,
    val targetPlanetUUID: UUID
) : Command(robotUUID)
