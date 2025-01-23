package com.ys.redis

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * StringRedisSerializer 으로 전부 다루는 이유
 * String 으로 바꾸어 다루는것이 가장 유연하다고 판단 (개인적인 의견)
 *
 * Redis 에 Object 혹은 여러 데이터를 넣기 위해 직렬화/비직렬화 하는 방법에는 여러가지 방법이 있습니다.
 * 하지만 Object 별로 RedisTemplate 를 매번 따로 만들어주어야 하거나, pakage 경로가 들어가거나, 직렬화 관련 인터페이스를 상속해야하는등 하나씩 이슈가 있습니다.
 * 이에 대한 방안으로 아예 Object 를 Json 형태로 변경하여 String 으로 만들어 Redis 에서 in/out 하는 방법을 선택 했습니다.
 */
@Configuration
@EnableCaching
class RedisConfig(
    @Value("\${spring.redis.host}")
    val host: String,

    @Value("\${spring.redis.port}")
    val port: Int,

    @Value("\${spring.redis.password}")
    val password: String,

    ) {

    @Primary
    @Bean("redisConnectionFactory")
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(host, port)
    }

    @Bean("redisConnectionFactoryWithPassword")
    fun redisConnectionFactoryWithPassword(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(
            host,
            port
        )

        redisStandaloneConfiguration.password = RedisPassword.of(password)

        return LettuceConnectionFactory(
            redisStandaloneConfiguration
        )
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            this.connectionFactory = redisConnectionFactory()

            this.keySerializer = StringRedisSerializer()
            this.valueSerializer = StringRedisSerializer()

            this.hashKeySerializer = StringRedisSerializer()
            this.hashValueSerializer = StringRedisSerializer()
        }
    }

    @Bean
    fun redisToJsonTemplate(@Qualifier("redisConnectionFactory") connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
        return RedisTemplate<Any, Any>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer() // JSON 포맷으로 저장
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = GenericJackson2JsonRedisSerializer()

        }
    }

    @Bean("stringJsonRedisTemplate")
    fun stringJsonRedisTemplate(redisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        val template = StringRedisTemplate()
        template.connectionFactory = redisConnectionFactory

        // 값 직렬화 방식을 JSON으로 설정
        val jsonSerializer = GenericJackson2JsonRedisSerializer(ObjectMapperConfig.objectMapper())
        template.valueSerializer = jsonSerializer
        template.hashValueSerializer = jsonSerializer

        // 키 직렬화 방식을 String으로 설정
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()

        return template
    }

    @Bean
    fun stringLongRedisTemplate(@Qualifier("redisConnectionFactory") connectionFactory: RedisConnectionFactory): RedisTemplate<String, Long> {
        val redisTemplate = RedisTemplate<String, Long>()
        redisTemplate.connectionFactory = connectionFactory
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = GenericToStringSerializer(Long::class.java)

        return redisTemplate
    }

    @Bean
    fun stringRedisTemplate(redisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        return StringRedisTemplate().apply {
            connectionFactory = redisConnectionFactory
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
        }
    }

}