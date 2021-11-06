package com.msd.command

import java.util.*

class MovementCommand(
    val playerUUID: UUID,
    robotUUID: UUID,
    val targetPlanetUUID: UUID
) : Command(robotUUID)
