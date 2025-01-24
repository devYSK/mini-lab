package com.ys.springbootdistributedlock.infra

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.time.Duration


@Component
class RedisRepository(
    private val redisTemplate: RedisTemplate<String, String>
) {

    // setIfAbsent() 를 활용해서 SETNX를 실행함.
    fun lock(key: String, timeoutMills: Long): Boolean {
        return redisTemplate
            .opsForValue()
            .setIfAbsent(key, "lock", Duration.ofMillis(timeoutMills)) ?: true
    }

    fun unlock(key: String): Boolean {
        return redisTemplate.delete(key)
    }

}