package com.github.simokhin.preschooltransfer.bot

import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext

typealias TgApiBotCommand = dev.inmo.tgbotapi.types.BotCommand

interface BotRegistration {
    suspend fun register(behaviourContext: BehaviourContext)
}
