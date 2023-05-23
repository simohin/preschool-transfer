package com.github.simokhin.preschooltransfer.service

import com.github.simokhin.preschooltransfer.client.EduInformTransferClient
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

private const val FIND_METHOD_CACHE_NAME = "AdministrativeOrganizationsService.getAll"
private const val GET_ALL_METHOD_CACHE_NAME = "AdministrativeOrganizationsService.getAll"

@Service
class AdministrativeOrganizationsService(
    private val client: EduInformTransferClient,
) {

    @Cacheable(FIND_METHOD_CACHE_NAME, key = "id")
// fixme always returns null
    fun find(id: UUID) = getAll()[id]

    @Cacheable(GET_ALL_METHOD_CACHE_NAME)
    fun getAll() = client.getAdministrativeOrganizations().associateBy { it.id }

    @CacheEvict(allEntries = true, value = [GET_ALL_METHOD_CACHE_NAME])
    fun refresh() {
    }
}
