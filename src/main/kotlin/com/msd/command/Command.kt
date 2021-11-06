package com.msd.command

import java.util.*

open class Command(
    val robotId: UUID
) {
    val transactionUUID = UUID.randomUUID()
}
