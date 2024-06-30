package com.yscorp.kotlinsharp

import org.slf4j.LoggerFactory
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.reflect
import kotlin.system.measureTimeMillis

object TimeLogging {
    private val log = LoggerFactory.getLogger(javaClass)

    @OptIn(ExperimentalReflectionOnLambdas::class)
    suspend fun <T> runWithTimingLogging(block: suspend () -> T): T {
        val time = measureTimeMillis {
            block()
        }
        log.info("Task completed in $time ms, ${block.reflect()?.name}")

        return block()
    }

}