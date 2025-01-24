package com.ys.springbootdistributedlock

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootDistributedLockApplication

fun main(args: Array<String>) {
    runApplication<SpringBootDistributedLockApplication>(*args)
}
