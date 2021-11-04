package com.msd.robot.application

import java.util.*

open class Command(
    val robotId: UUID
) {
    val transactionUUID = UUID.randomUUID()
}
