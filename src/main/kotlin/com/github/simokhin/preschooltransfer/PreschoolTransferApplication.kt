package com.github.simokhin.preschooltransfer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class PreschoolTransferApplication

fun main(args: Array<String>) {
    runApplication<PreschoolTransferApplication>(*args)
}
