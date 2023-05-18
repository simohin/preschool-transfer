package com.github.simokhin.preschooltransfer.schedule

import java.time.OffsetDateTime

interface ScheduledTask : Runnable {

    var lastRun: OffsetDateTime

    fun needExecute(): Boolean

    override fun run() = execute().also { lastRun = OffsetDateTime.now() }

    fun execute()
}
