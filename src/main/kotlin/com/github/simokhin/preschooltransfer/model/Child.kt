package com.github.simokhin.preschooltransfer.model

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class Child(
    @field:JsonProperty
    val lastName: String,
    @field:JsonProperty
    val firstName: String,
    @field:JsonProperty
    val surName: String,
    @field:JsonProperty
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    val birthDate: LocalDate,
)
