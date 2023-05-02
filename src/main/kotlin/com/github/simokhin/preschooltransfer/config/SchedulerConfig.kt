package com.github.simokhin.preschooltransfer.config

import com.github.simokhin.preschooltransfer.client.EduInformTransferClient
import com.github.simokhin.preschooltransfer.dto.FreePlaceRequest
import com.github.simokhin.preschooltransfer.model.Preschool
import com.github.simokhin.preschooltransfer.util.parallelMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.util.UUID

@Configuration
@EnableScheduling
class SchedulerConfig(
    private val eduInformTransferClient: EduInformTransferClient,
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(this::class.simpleName)
        private val pupilId = UUID.fromString("76331155-fff4-40a7-8089-bad9a8b3e95f")
        private const val FREE_PLACE_CHUNK_SIZE = 10
    }

    @Scheduled(fixedDelay = 10000)
    fun getPreschools() = with(eduInformTransferClient.getPreschools().associateBy(Preschool::id)) {
        runBlocking(Dispatchers.Default) {
            entries
                .chunked(FREE_PLACE_CHUNK_SIZE)
                .parallelMap { entry ->
                    val map = mutableMapOf<UUID, Preschool>()
                    entry.associateByTo(map, { it.key }, { it.value })
                    withContext(Dispatchers.IO) {
                        eduInformTransferClient.getFreePlace(FreePlaceRequest(pupilId, map.keys))
                    }
                        .filter { it.availableGroupIds.isNotEmpty() }
                        .associateBy { it.id }
                }.reduce { acc, map -> acc + map }
        }.forEach { log.info("${this[it.key]!!.shortCaption}: Колличество групп: ${it.value.availableGroupIds.size}") }
    }
}
