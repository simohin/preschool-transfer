package com.github.simokhin.preschooltransfer.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class Preschool(
    @field:JsonProperty
    val id: UUID,
    @field:JsonProperty
    val shortCaption: String,
    @field:JsonProperty
    val administrativeOrganizationId: UUID,
)
