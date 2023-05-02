package com.github.simokhin.preschooltransfer.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.simokhin.preschooltransfer.dto.EduInformError
import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.stereotype.Component

@Component
class ConfiguredErrorDecoder(
    private val mapper: ObjectMapper,
) : ErrorDecoder {
    override fun decode(methodKey: String?, response: Response): Exception {

        val error = try {
            mapper.readValue(response.body().asInputStream(), EduInformError::class.java)
        } catch (e: Exception) {
            return e
        }

        return EduInformException(error)
    }
}

data class EduInformException(
    val error: EduInformError,
) : RuntimeException(error.toString())
