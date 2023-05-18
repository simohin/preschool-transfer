package com.github.simokhin.preschooltransfer.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.UUID

data class AdministrativeOrganization(
    @field:JsonProperty
    val id: UUID,
    @field:JsonProperty
    val shortCaption: String,
    @field:JsonProperty
    val territoryCaption: String,
) : Preservable
