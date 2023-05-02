package com.github.simokhin.preschooltransfer.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class FreePlaceResponseItem(
    @field:JsonProperty
    val id: UUID,
    @field:JsonProperty
    val availableGroupIds: Set<UUID>,
)
