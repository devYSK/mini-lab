package com.ys.springbootdistributedlock.common

import com.ys.springbootdistributedlock.common.CustomSpringELParser.getDynamicValue
import com.ys.springbootdistributedlock.config.RedissonConfig.Companion.REDISSON_LOCK_PREFIX
import com.ys.springbootdistributedlock.config.logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RedissonClient
import org.springframework.core.annotation.Order
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import org.springframework.stereotype.Component
import java.lang.reflect.Method


@Component
@Aspect
@Order(value = 1)
class DistributedLockAspect(
    private val redissonClient: RedissonClient,
    private val aopForTransaction: AopForTransaction,
) {

    private val log = logger()

    @Around("@annotation(com.ys.springbootdistributedlock.common.DistributedLock)")
    @Throws(Throwable::class)
    fun lock(joinPoint: ProceedingJoinPoint): Any? {

        val signature: MethodSignature = joinPoint.signature as MethodSignature
        val method: Method = signature.method

        val lockAnnotation: DistributedLock = method.getAnnotation(DistributedLock::class.java)

        val key =
            REDISSON_LOCK_PREFIX + getDynamicValue(signature.parameterNames, joinPoint.args, lockAnnotation.key)

        val rLock = redissonClient.getLock(key)

        try {
            val available = rLock.tryLock(
                lockAnnotation.waitTime,
                lockAnnotation.leaseTime,
                lockAnnotation.timeUnit
            ) // (2)

            if (!available) {
                return false
            }

            return aopForTransaction.proceed(joinPoint)

        } catch (e: Exception) {
            println(e.message + ", ${e::class.java.name}")
            throw InterruptedException()
        } finally {
            try {
                rLock.unlock()   // (4)
            } catch (e: IllegalMonitorStateException) {
                log.info("Redisson Lock Already UnLock serviceName : ${method.name}, key :${key}")
            }
        }
    }
}

object CustomSpringELParser {

    fun getDynamicValue(parameterNames: Array<String>, args: Array<Any>, key: String): Any? {
        val parser: ExpressionParser = SpelExpressionParser()
        val context = StandardEvaluationContext()

        for (i in parameterNames.indices) {
            context.setVariable(parameterNames[i], args[i])
        }

        return parser.parseExpression(key).getValue(context, Any::class.java)
    }

}