package com.github.simokhin.preschooltransfer.schedule.task

import com.github.simokhin.preschooltransfer.config.TgBot
import com.github.simokhin.preschooltransfer.service.AdministrativeOrganizationsService
import com.github.simokhin.preschooltransfer.service.FreePlaceService
import com.github.simokhin.preschooltransfer.service.PreschoolsService
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.ChatId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SendFreePlaceInfoTask(
    @Value("\${admin.chats:}")
    private val chatIds: List<Long>,
    @Value("\${admin.pupil.id:}")
    private val defaultPupilId: UUID,
    private val bot: TgBot,
    private val freePlaceService: FreePlaceService,
    private val preschoolsService: PreschoolsService,
    private val administrativeOrganizationsService: AdministrativeOrganizationsService,
) {

    init {
        execute()
    }

    @Scheduled(cron = "\${scheduled.task.rate.free.place.info:0 11 * * * *}")
    final fun execute() {
        runBlocking(Dispatchers.IO) {

            val preschools = preschoolsService.getAll()
                .associateBy { preschool -> preschool.id }

            val freePlaces = (freePlaceService.getAll(defaultPupilId) ?: return@runBlocking)
                .map { entry ->
                    val administrativeOrganization = preschools[entry.key]!!
                        .administrativeOrganizationId
                        .let { administrativeOrganizationsService.find(it) }
                    administrativeOrganization to "${preschools[entry.value.id]!!.shortCaption}: ${entry.value.availableGroupIds.size}"
                }
                .groupBy { it.first }
                .map {
                    it.key to it.value.map { pair -> pair.second }.joinToString { "\n" }
                }

            chatIds.forEach { chatId ->
                freePlaces.forEach {
                    bot.execute(
                        SendTextMessage(
                            ChatId(chatId),
                            "Найдены свободные места в районе ${it.first.territoryCaption}:\n${it.second}"
                        )
                    )
                }
            }
        }
    }
}
