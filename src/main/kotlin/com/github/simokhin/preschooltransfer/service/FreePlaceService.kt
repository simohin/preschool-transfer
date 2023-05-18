package com.github.simokhin.preschooltransfer.service

import com.github.simokhin.preschooltransfer.client.EduInformTransferClient
import com.github.simokhin.preschooltransfer.dto.FreePlaceRequest
import com.github.simokhin.preschooltransfer.model.Preschool
import com.github.simokhin.preschooltransfer.util.parallelMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FreePlaceService(
    private val eduInformTransferClient: EduInformTransferClient,
    private val preschoolsService: PreschoolsService,
) {

    companion object {
        const val FREE_PLACE_CACHE_NAME = "free-place"
    }

    @Cacheable(FREE_PLACE_CACHE_NAME)
    fun getAll(pupilId: UUID) = with(preschoolsService.getAll().associateBy(Preschool::id)) {
        try {
            runBlocking(Dispatchers.IO) {
                entries.chunked(10)
                    .parallelMap { entry ->
                        val map = mutableMapOf<UUID, Preschool>()
                        entry.associateByTo(map, { it.key }, { it.value })
                        withContext(Dispatchers.IO) {
                            eduInformTransferClient.getFreePlace(FreePlaceRequest(pupilId, map.keys))
                        }
                            .filter { it.availableGroupIds.isNotEmpty() }
                            .associateBy { it.id }
                    }.reduce { acc, map -> acc + map }
            }
        } catch (e: Exception) {
            return@with null
        }
    }

    @CacheEvict(allEntries = true, value = [FREE_PLACE_CACHE_NAME])
    @Scheduled(fixedDelay = 60 * 60 * 1000)
    fun evictCache() {
    }
}
