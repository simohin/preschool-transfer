package com.github.simokhin.preschooltransfer.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class FreePlaceRequest(
    @field:JsonProperty
    val pupilId: UUID,
    @field:JsonProperty
    val preschoolIds: Set<UUID>,
)
