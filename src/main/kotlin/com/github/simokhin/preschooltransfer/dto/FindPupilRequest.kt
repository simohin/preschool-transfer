package com.github.simokhin.preschooltransfer.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.simokhin.preschooltransfer.model.Child
import java.util.UUID

data class FindPupilRequest(
    @field:JsonProperty
    val child: Child,
    @field:JsonProperty
    val preschoolId: UUID,
)
