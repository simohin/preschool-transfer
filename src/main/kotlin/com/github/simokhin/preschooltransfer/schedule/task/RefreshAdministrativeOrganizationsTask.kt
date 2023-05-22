package com.github.simokhin.preschooltransfer.schedule.task

import com.github.simokhin.preschooltransfer.service.AdministrativeOrganizationsService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RefreshAdministrativeOrganizationsTask(
    private val service: AdministrativeOrganizationsService,
) {

    @Scheduled(cron = "\${scheduled.task.rate.refresh.administrative.organizations:0 11 * * * *}")
    fun execute() {
        service.refresh()
    }
}
