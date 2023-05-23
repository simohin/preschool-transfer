package com.github.simokhin.preschooltransfer.service

import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.ChatId
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.InvalidParameterException
import java.util.UUID

@Service
class NotificationBuilderService(
    private val preschoolsService: PreschoolsService,
    private val freePlaceService: FreePlaceService,
    private val administrativeOrganizationsService: AdministrativeOrganizationsService,
    @Value("\${admin.pupil.id:}")
    private val defaultPupilId: UUID,
) {

    fun buildNotificationMessage(chatId: Long, preschoolIds: Collection<UUID>): SendTextMessage {
        if (preschoolIds.isEmpty()) throw InvalidParameterException()
        val preschools = preschoolsService.getAll().associateBy { it.id }

        val freePlaces = freePlaceService.getAll(defaultPupilId).values.joinToString("\n") {
            "${preschools[it.id]!!.shortCaption}: колличество групп - ${it.availableGroupIds.size}"
        }

        return SendTextMessage(ChatId(chatId), "Найдены свободные места:\n$freePlaces")
    }

    fun buildFreePlaceInfoMessages(chatId: Long) = buildFreePlaceInfoMessages(setOf(chatId))

    fun buildFreePlaceInfoMessages(chatIds: Collection<Long>): List<SendTextMessage> {
        val preschools = preschoolsService.getAll()
            .associateBy { preschool -> preschool.id }

        val freePlaces = freePlaceService.getAll(defaultPupilId)
        val administrativeOrganizations = administrativeOrganizationsService.getAll()

        val administrativeOrganizationToFreePlaces = freePlaces.map {
            preschools[it.key]!! to it.value.availableGroupIds.size
        }
            .groupBy { it.first.administrativeOrganizationId }
            .mapKeys { administrativeOrganizations[it.key]!!.territoryCaption }
            .mapValues {
                it.value.joinToString("\n") { pair -> "${pair.first.shortCaption}: колличество групп - ${pair.second}" }
            }

        return chatIds.flatMap { chatId ->
            administrativeOrganizationToFreePlaces.map {
                SendTextMessage(
                    ChatId(chatId),
                    "Свободные места в районе ${it.key}:\n${it.value}"
                )
            }
        }
    }
}
