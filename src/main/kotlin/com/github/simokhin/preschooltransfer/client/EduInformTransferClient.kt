package com.github.simokhin.preschooltransfer.client

import com.github.simokhin.preschooltransfer.dto.FindPupilRequest
import com.github.simokhin.preschooltransfer.dto.FindPupilResponse
import com.github.simokhin.preschooltransfer.dto.FreePlaceRequest
import com.github.simokhin.preschooltransfer.dto.FreePlaceResponseItem
import com.github.simokhin.preschooltransfer.model.Preschool
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(value = "edu-inform", url = "https://edu-inform.ekadm.ru/psceq_transfer/api/transfer/")
interface EduInformTransferClient {

    @RequestMapping(method = [RequestMethod.POST], value = ["/findpupil"])
    fun findPupil(request: FindPupilRequest): FindPupilResponse

    @RequestMapping(method = [RequestMethod.POST], value = ["/preschools/freeplace"])
    fun getFreePlace(request: FreePlaceRequest): Set<FreePlaceResponseItem>

    @RequestMapping(method = [RequestMethod.GET], value = ["/preschools"])
    fun getPreschools(): Set<Preschool>
}
