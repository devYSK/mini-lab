package com.ys.springbootdistributedlock.common

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributedLock(

    val key: String,

    val timeUnit: TimeUnit = TimeUnit.SECONDS,

    val waitTime: Long = 10L,

    val leaseTime: Long = 10L

)
