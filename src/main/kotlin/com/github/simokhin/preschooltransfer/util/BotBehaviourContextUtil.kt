package com.github.simokhin.preschooltransfer.util

import com.github.simokhin.preschooltransfer.model.AdministrativeOrganization
import com.github.simokhin.preschooltransfer.model.Preschool
import dev.inmo.tgbotapi.extensions.behaviour_builder.BehaviourContext
import dev.inmo.tgbotapi.extensions.behaviour_builder.expectations.waitDataCallbackQuery
import dev.inmo.tgbotapi.requests.send.SendTextMessage
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardButtons.CallbackDataInlineKeyboardButton
import dev.inmo.tgbotapi.types.buttons.InlineKeyboardMarkup
import dev.inmo.tgbotapi.types.queries.callback.DataCallbackQuery
import dev.inmo.tgbotapi.utils.matrix
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.UUID

suspend fun BehaviourContext.getAdministrativeOrganization(
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

suspend fun BehaviourContext.getPreschool(
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
