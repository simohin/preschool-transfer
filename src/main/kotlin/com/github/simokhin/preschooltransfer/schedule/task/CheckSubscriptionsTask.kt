package com.github.simokhin.preschooltransfer.schedule.task

import com.github.simokhin.preschooltransfer.schedule.ScheduledTask
import com.github.simokhin.preschooltransfer.service.FreePlaceService
import com.github.simokhin.preschooltransfer.service.NotificationService
import com.github.simokhin.preschooltransfer.service.SubscriptionService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.OffsetDateTime

@Component
class CheckSubscriptionsTask(
    @Value("\${scheduled.task.rate.check.subscription:PT3H}")
    private val rate: Duration,
    private val subscriptionService: SubscriptionService,
    private val freePlaceService: FreePlaceService,
    private val notificationService: NotificationService,
) : ScheduledTask {

    override var lastRun: OffsetDateTime = OffsetDateTime.MIN
    override fun needExecute() = OffsetDateTime.now().isAfter(lastRun.plus(rate))

    override fun execute() {
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
            notificationService.notificate(it.first.chatId, it.second)
        }
    }
}
