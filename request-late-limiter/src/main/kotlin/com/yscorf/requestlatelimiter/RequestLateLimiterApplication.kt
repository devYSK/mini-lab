package com.yscorf.requestlatelimiter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RequestLateLimiterApplication

fun main(args: Array<String>) {
    runApplication<RequestLateLimiterApplication>(*args)
}
