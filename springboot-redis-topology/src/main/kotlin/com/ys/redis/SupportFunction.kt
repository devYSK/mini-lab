package com.ys.redis

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.util.StringUtils
import java.time.Duration

object SupportFunction {

    fun <T> RedisTemplate<String, Any>.get(key: String, classType: Class<T>, objectMapper: ObjectMapper? = null): T? {
        val jsonData = this.opsForValue()[key] as String?

        return try {
            if (StringUtils.hasText(jsonData)) {
                if (objectMapper == null) {
                    ObjectMapper().readValue(jsonData, classType)
                } else {
                    objectMapper.readValue(jsonData, classType)
                }
            } else {
                null
            }
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }

    fun <T> RedisTemplate<String, Any>.set(key: String, data: T, timeout: Duration? = null, objectMapper: ObjectMapper? = null) {
        val setData = if (objectMapper == null) {
            ObjectMapper().writeValueAsString(data)
        } else {
            objectMapper.writeValueAsString(data)
        }

        if (timeout != null) {
            this.opsForValue().set(key, setData, timeout)
        } else {
            this.opsForValue().set(key, setData)
        }

    }
}
