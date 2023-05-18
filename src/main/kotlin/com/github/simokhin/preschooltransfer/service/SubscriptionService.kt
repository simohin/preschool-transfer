package com.github.simokhin.preschooltransfer.service

import com.github.simokhin.preschooltransfer.model.Subscription
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class SubscriptionService {

    private val subscriptions: Set<Subscription> = mutableSetOf()

    fun subscribe(chatId: Long, pupilId: UUID, preschoolIds: Set<UUID>) {
        subscriptions.plus(Subscription(chatId, pupilId, preschoolIds))
    }

    fun getAll() = subscriptions
}
