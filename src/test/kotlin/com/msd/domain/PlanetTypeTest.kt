package com.msd.domain

import com.msd.planet.domain.Planet
import com.msd.planet.domain.PlanetType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.util.*

class PlanetTypeTest {

    @Test
    fun `Spawn points must have a playerId`() {
        assertThrows<NullPointerException> { Planet(UUID.randomUUID(), PlanetType.SPAWN) }
        assertDoesNotThrow { Planet(UUID.randomUUID(), PlanetType.SPAWN, UUID.randomUUID()) }
    }

    @Test
    fun `Only spawn points have a playerId`() {
        assertThrows<IllegalArgumentException>("Only Spawns can have a playerId") {
            Planet(UUID.randomUUID(), PlanetType.SPACE_STATION, UUID.randomUUID())
        }
        assertDoesNotThrow { Planet(UUID.randomUUID(), PlanetType.SPAWN, UUID.randomUUID()) }
    }
}
