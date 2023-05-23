package com.github.simokhin.preschooltransfer.schedule.task

import com.github.simokhin.preschooltransfer.config.TgBot
import com.github.simokhin.preschooltransfer.service.FreePlaceService
import com.github.simokhin.preschooltransfer.service.NotificationBuilderService
import com.github.simokhin.preschooltransfer.service.SubscriptionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CheckSubscriptionsTask(
    private val subscriptionService: SubscriptionService,
    private val freePlaceService: FreePlaceService,
    private val notificationBuilderService: NotificationBuilderService,
    private val bot: TgBot,
) {

    @Scheduled(cron = "\${scheduled.task.rate.check.subscription:0 */3 * * * *}")
    fun execute() {
        val subscriptions = subscriptionService.getAll().takeIf { it.isNotEmpty() } ?: return

        val freePlaces = subscriptions.firstNotNullOf {
            freePlaceService.getAll(it.pupilId)
        }

        subscriptions.mapNotNull { subscription ->
            val availablePreschoolIds = subscription.preschoolIds.filter { preschoolId ->
                freePlaces.containsKey(preschoolId)
            }

            return@mapNotNull if (availablePreschoolIds.isNotEmpty()) {
                subscription to availablePreschoolIds
            } else null
        }.forEach {
            runBlocking(Dispatchers.IO) {
                bot.execute(notificationBuilderService.buildNotificationMessage(it.first.chatId, it.second))
            }
        }
    }
}
