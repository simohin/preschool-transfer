package com.github.simokhin.preschooltransfer.dto

data class EduInformError(
    val message: String,
    val code: String?,
    val data: EduInformErrorData?,
)

typealias EduInformErrorData = Map<String, Collection<String>>
