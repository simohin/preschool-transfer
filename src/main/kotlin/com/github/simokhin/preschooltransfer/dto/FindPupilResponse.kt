package com.github.simokhin.preschooltransfer.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class FindPupilResponse(
    @field:JsonProperty
    val id: UUID,
)
