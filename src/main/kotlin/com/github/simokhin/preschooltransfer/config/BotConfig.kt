package com.github.simokhin.preschooltransfer.config

import com.github.simokhin.preschooltransfer.bot.BotRegistration
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.bot.ktor.telegramBot
import dev.inmo.tgbotapi.extensions.behaviour_builder.buildBehaviourWithLongPolling
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

typealias TgBotCoroutineScope = CoroutineScope
typealias TgBot = TelegramBot

@Configuration
class BotConfig(
    private val commands: List<BotRegistration>,
    @Value("\${tg.bot.token}")
    private val token: String,
) {

    private val scope: TgBotCoroutineScope = CoroutineScope(Dispatchers.Default)
    private val bot: TgBot = telegramBot(token)
    private val handler = CoroutineExceptionHandler { _, e -> log.error("Error handled", e) }

    companion object {
        private val log = LoggerFactory.getLogger(BotConfig::class.simpleName)
    }

    @PostConstruct
    fun init() = scope.async(handler) {
        bot.buildBehaviourWithLongPolling {
            commands.forEach { command ->
                command.register(this)
            }
        }.join()
    }

    @Bean
    fun bot() = bot

    @Bean
    fun scope() = scope
}
