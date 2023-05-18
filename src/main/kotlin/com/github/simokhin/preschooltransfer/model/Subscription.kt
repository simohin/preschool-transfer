package com.github.simokhin.preschooltransfer.model

import java.util.UUID

data class Subscription(
    val chatId: Long,
    val pupilId: UUID,
    val preschoolIds: Set<UUID>,
)
