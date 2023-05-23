package com.github.simokhin.preschooltransfer.bot.command

import com.github.simokhin.preschooltransfer.bot.BotRegistration
import com.github.simokhin.preschooltransfer.bot.TgApiBotCommand
import com.github.simokhin.preschooltransfer.service.NotificationBuilderService
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import org.springframework.stereotype.Component

@Component
class GetFreePlaceInfoCommandRegistration(
    private val notificationBuilderService: NotificationBuilderService,
) : BotRegistration {
        private val command = TgApiBotCommand("freeplace", "Запрашивает список свободных мест")

    override suspend fun register(behaviourContext: BehaviourContext) {
        behaviourContext.onCommand(command) {
            notificationBuilderService.buildFreePlaceInfoMessages(it.chat.id.chatId)
                .forEach { message ->
                    execute(message)
                }
        }
    }
}
