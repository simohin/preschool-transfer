package com.github.simokhin.preschooltransfer.service

import com.github.simokhin.preschooltransfer.client.EduInformTransferClient
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

private const val GET_ALL_METHOD_CACHE_NAME = "PreschoolsService.getAll"

@Service
class PreschoolsService(
    private val client: EduInformTransferClient,
) {

    @Cacheable(GET_ALL_METHOD_CACHE_NAME)
    fun getAll() = client.getPreschools()

    @CacheEvict(allEntries = true, value = [GET_ALL_METHOD_CACHE_NAME])
    fun refresh() {
    }
}
