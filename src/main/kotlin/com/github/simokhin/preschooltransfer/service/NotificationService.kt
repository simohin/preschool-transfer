package com.github.simokhin.preschooltransfer.service

import com.github.simokhin.preschooltransfer.config.TgBot
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.ChatId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationService(
    private val bot: TgBot,
    private val preschoolsService: PreschoolsService,
    private val freePlaceService: FreePlaceService,
    @Value("\${admin.pupil.id:}")
    private val defaultPupilId: UUID,
) {

    fun notificate(chatId: Long, preschoolIds: Collection<UUID>) {
        if (preschoolIds.isNotEmpty()) return
        val preschools = preschoolsService.getAll().associateBy { it.id }

        val freePlaces = (freePlaceService.getAll(defaultPupilId) ?: return).map {
            "${preschools[it.value.id]!!.shortCaption}: ${it.value.availableGroupIds.size}"
        }.joinToString("\n")

        runBlocking(Dispatchers.IO) {
            bot.execute(SendTextMessage(ChatId(chatId), "Найдены свободные места:\n$freePlaces"))
        }
    }
}
