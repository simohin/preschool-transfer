package com.github.simokhin.preschooltransfer.schedule.task

import com.github.simokhin.preschooltransfer.config.TgBot
import com.github.simokhin.preschooltransfer.service.NotificationBuilderService
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class SendFreePlaceInfoTask(
    @Value("\${admin.chats:}")
    private val chatIds: List<Long>,
    private val notificationBuilderService: NotificationBuilderService,
    private val bot: TgBot
) {

    @PostConstruct
    fun postConstruct() = execute()

    @Scheduled(cron = "\${scheduled.task.rate.free.place.info:0 11 * * * *}")
    final fun execute() = notificationBuilderService.buildFreePlaceInfoMessages(chatIds)
}
