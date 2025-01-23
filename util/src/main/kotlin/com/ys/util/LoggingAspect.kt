package com.ys.util

import org.aspectj.lang.JoinPoint
import org.springframework.stereotype.Component


@org.aspectj.lang.annotation.Aspect
@Component
class LoggingAspect(val loggingProducer: LoggingProducer) {

    @org.aspectj.lang.annotation.Before("execution(* com.ys.*.adapter.in.web.*.*(..))")
    fun beforeMethodExecution(joinPoint: JoinPoint) {
        val methodName: String = joinPoint.signature.name
        loggingProducer.sendMessage("logging", "Before executing method: $methodName")
        // Produce Access log
    }

}