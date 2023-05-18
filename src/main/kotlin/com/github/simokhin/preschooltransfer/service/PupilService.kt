package com.github.simokhin.preschooltransfer.service

import com.github.simokhin.preschooltransfer.client.EduInformTransferClient
import com.github.simokhin.preschooltransfer.config.EduInformException
import com.github.simokhin.preschooltransfer.dto.FindPupilRequest
import com.github.simokhin.preschooltransfer.model.Child
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class PupilService(
    private val client: EduInformTransferClient,
) {

    companion object {
        private val log = LoggerFactory.getLogger(PupilService::class.simpleName)
    }

    fun find(lastName: String, firstName: String, surName: String, birthDate: LocalDate, preschoolId: UUID) =
        try {
            client.findPupil(
                FindPupilRequest(
                    Child(
                        lastName,
                        firstName,
                        surName,
                        birthDate
                    ),
                    preschoolId
                )
            )
        } catch (e: EduInformException) {
            log.error(e.error.toString())
            null
        }
}
