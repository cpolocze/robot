package com.msd.command

import java.util.*

/**
 * Describes the request of a player to move a specific [Robot] from one [Planet] to another.
 */
class MovementCommand(
    robotUUID: UUID,
    val targetPlanetUUID: UUID,
    val playerUUID: UUID
) : Command(robotUUID)
