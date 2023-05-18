package com.github.simokhin.preschooltransfer.schedule.task

import com.github.simokhin.preschooltransfer.config.TgBot
import com.github.simokhin.preschooltransfer.schedule.ScheduledTask
import com.github.simokhin.preschooltransfer.service.FreePlaceService
import com.github.simokhin.preschooltransfer.service.PreschoolsService
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.ChatId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.OffsetDateTime
import java.util.UUID

@Component
class SendFreePlaceInfoTask(
    @Value("\${admin.chats:}")
    private val chatIds: List<Long>,
    @Value("\${admin.pupil.id:}")
    private val defaultPupilId: UUID,
    @Value("\${scheduled.task.rate.free.place.info:PT24H}")
    private val rate: Duration,
    private val bot: TgBot,
    private val freePlaceService: FreePlaceService,
    private val preschoolsService: PreschoolsService,
) : ScheduledTask {
    override var lastRun: OffsetDateTime = OffsetDateTime.MIN
    override fun needExecute() = OffsetDateTime.now().isAfter(lastRun.plus(rate))

    override fun execute() {
        runBlocking(Dispatchers.IO) {

            val preschools = preschoolsService.getAll()
                .associateBy { preschool -> preschool.id }
            val freePlaces = (freePlaceService.getAll(defaultPupilId) ?: return@runBlocking).map {
                "${preschools[it.value.id]!!.shortCaption}: ${it.value.availableGroupIds.size}"
            }.joinToString("\n")

            chatIds.forEach { chatId ->
                bot.execute(SendTextMessage(ChatId(chatId), "Найдены свободные места:\n$freePlaces"))
            }
        }
    }
}
