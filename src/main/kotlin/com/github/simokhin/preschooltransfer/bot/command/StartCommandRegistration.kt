package com.github.simokhin.preschooltransfer.bot.command

import com.github.simokhin.preschooltransfer.bot.BotRegistration
import com.github.simokhin.preschooltransfer.bot.TgApiBotCommand
import dev.inmo.tgbotapi.extensions.api.send.reply
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import org.springframework.stereotype.Component

@Component
class StartCommandRegistration : BotRegistration {
    private val command = TgApiBotCommand("start", "Начинает работу бота")

    override suspend fun register(behaviourContext: BehaviourContext) {
        behaviourContext.onCommand(command) {
            reply(it, "Добро пожаловать!")
        }
    }
}
