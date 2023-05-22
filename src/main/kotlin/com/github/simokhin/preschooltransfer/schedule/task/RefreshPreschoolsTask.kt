package com.github.simokhin.preschooltransfer.schedule.task

import com.github.simokhin.preschooltransfer.service.PreschoolsService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RefreshPreschoolsTask(
    private val service: PreschoolsService,
) {
    @Scheduled(cron = "\${scheduled.task.rate.refresh.preschools:0 11 * * * *}")
    fun execute() {
        service.refresh()
    }
}
