package com.github.simokhin.preschooltransfer.schedule.task

import com.github.simokhin.preschooltransfer.schedule.ScheduledTask
import com.github.simokhin.preschooltransfer.service.AdministrativeOrganizationsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.OffsetDateTime

@Component
class RefreshAdministrativeOrganizationsTask(
    @Value("\${scheduled.task.rate.refresh.administrative.organizations:PT24H}")
    private val rate: Duration,
    private val service: AdministrativeOrganizationsService,
) : ScheduledTask {

    override var lastRun: OffsetDateTime = OffsetDateTime.MIN
    override fun needExecute() = OffsetDateTime.now().isAfter(lastRun.plus(rate))
    override fun execute() {
        service.refresh()
    }
}
