package com.github.simokhin.preschooltransfer.config

import com.github.simokhin.preschooltransfer.schedule.ScheduledTask
import com.github.simokhin.preschooltransfer.util.runParallel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled

@Configuration
@EnableScheduling
class SchedulerConfig(
    private val tasks: List<ScheduledTask>,
) {

    @Scheduled(cron = "\${scheduled.main.cron:*/10 * * * * *}")
    fun runTasks() = runBlocking(Dispatchers.IO) { tasks.filter { it.needExecute() }.runParallel() }
}
