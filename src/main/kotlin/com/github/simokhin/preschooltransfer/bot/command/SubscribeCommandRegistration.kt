package com.github.simokhin.preschooltransfer.bot.command

import com.github.simokhin.preschooltransfer.bot.BotRegistration
import com.github.simokhin.preschooltransfer.bot.TgApiBotCommand
import com.github.simokhin.preschooltransfer.model.AdministrativeOrganization
import com.github.simokhin.preschooltransfer.model.Preschool
import com.github.simokhin.preschooltransfer.service.AdministrativeOrganizationsService
import com.github.simokhin.preschooltransfer.service.PreschoolsService
import com.github.simokhin.preschooltransfer.service.PupilService
import com.github.simokhin.preschooltransfer.service.SubscriptionService
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitDataCallbackQuery
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitText
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.matrix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

@Component
class SubscribeCommandRegistration(
    private val administrativeOrganizationsService: AdministrativeOrganizationsService,
    private val preschoolsService: PreschoolsService,
    private val pupilService: PupilService,
    private val subscriptionService: SubscriptionService,
) : BotRegistration {
    private val command = TgApiBotCommand("subscribe", "Подписаться на место в садиках")

    private val log = LoggerFactory.getLogger(this::class.simpleName)

    private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override suspend fun register(behaviourContext: BehaviourContext) {
        behaviourContext.onCommand(command) {
            val chatId = it.chat.id

            val organizations = administrativeOrganizationsService.getAll()
                .associateBy { organization -> organization.id }

            val preschools = preschoolsService.getAll()
                .associateBy { preschool -> preschool.id }

            val currentAdministrativeOrganization =
                getAdministrativeOrganization("Выберите текущий район:", chatId, organizations)

            val currentPreschool =
                getPreschool("Выберите текущий садик", chatId, preschools, currentAdministrativeOrganization)
                    .map { query -> preschools[UUID.fromString(query.data)] }
                    .first()!!

            val lastName = waitText(
                SendTextMessage(chatId, "Фамилия:")
            ).first().text

            val firstName = waitText(
                SendTextMessage(chatId, "Имя:")
            ).first().text

            val surName = waitText(
                SendTextMessage(chatId, "Отчество:")
            ).first().text

            val birthDate = waitText(
                SendTextMessage(chatId, "Дата рождения в формате 31.12.1999")
            ).first().text

            val pupilResponse = pupilService.find(
                lastName,
                firstName,
                surName,
                LocalDate.parse(birthDate, dateTimeFormatter),
                currentPreschool.id
            ) ?: throw RuntimeException()

            val nextAdministrativeOrganization =
                getAdministrativeOrganization("Выберите новый район:", chatId, organizations)

            var done = false
            val nextPreschools = mutableSetOf<Preschool>()

            while (nextPreschools.size < 10 && !done) {
                val doneText = "Готово"
                val data = getPreschool(
                    "Выберите новый садик (до 10 штук) или нажмите \"$doneText\"",
                    chatId,
                    preschools,
                    nextAdministrativeOrganization,
                    doneText
                ).first().data

                done = data == doneText
                if (!done) {
                    nextPreschools.add(preschools[UUID.fromString(data)]!!)
                }
            }

            subscriptionService.subscribe(chatId.chatId, pupilResponse.id, nextPreschools.map(Preschool::id).toSet())

            log.info(nextPreschools.map { preschool -> preschool.shortCaption }.toString())
        }
    }

    private suspend fun BehaviourContext.getAdministrativeOrganization(
        text: String,
        chatId: IdChatIdentifier,
        organizations: Map<UUID, AdministrativeOrganization>,
    ) = waitDataCallbackQuery(
        SendTextMessage(
            chatId, text, replyMarkup = InlineKeyboardMarkup(
                matrix {
                    organizations.values.map { organization ->
                        CallbackDataInlineKeyboardButton(
                            organization.territoryCaption,
                            organization.id.toString()
                        )
                    }.forEach { button -> +button }
                }
            )
        )
    ).map { query -> organizations[UUID.fromString(query.data)] }.first()!!

    private suspend fun BehaviourContext.getPreschool(
        text: String,
        chatId: IdChatIdentifier,
        preschools: Map<UUID, Preschool>,
        currentAdministrativeOrganization: AdministrativeOrganization,
        vararg additionalButtons: String,
    ): Flow<DataCallbackQuery> = waitDataCallbackQuery(
        SendTextMessage(
            chatId, text, replyMarkup = InlineKeyboardMarkup(
                matrix {
                    val buttons = additionalButtons.toList().map {
                        CallbackDataInlineKeyboardButton(
                            it,
                            it
                        )
                    }
                    preschools.values
                        .filter { preschool -> preschool.administrativeOrganizationId == currentAdministrativeOrganization.id }
                        .map { organization ->
                            CallbackDataInlineKeyboardButton(
                                organization.shortCaption,
                                organization.id.toString()
                            )
                        }.plus(buttons)
                        .forEach { button -> +button }
                }
            )
        )
    )
}