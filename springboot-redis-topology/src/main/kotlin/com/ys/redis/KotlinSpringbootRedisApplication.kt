package com.ys.redis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KotlinSpringbootRedisApplication

fun main(args: Array<String>) {
    runApplication<KotlinSpringbootRedisApplication>(*args)
}
