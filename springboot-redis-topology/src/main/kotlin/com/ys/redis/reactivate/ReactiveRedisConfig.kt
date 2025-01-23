package com.ys.redis.reactivate

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.ys.redis.User
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer

@Configuration
class ReactiveRedisConfig {

    @Bean
    fun reactiveRedisUserTemplate(connectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, User> {
        val objectMapper = ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)

        val jsonSerializer: Jackson2JsonRedisSerializer<User> =
            Jackson2JsonRedisSerializer(objectMapper, User::class.java)

        val serializationContext: RedisSerializationContext<String, User> = RedisSerializationContext
            .newSerializationContext<String, User>()
            .key(RedisSerializer.string())
            .value(jsonSerializer)
            .hashKey(RedisSerializer.string())
            .hashValue(jsonSerializer)
            .build()
        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }
}