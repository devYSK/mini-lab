package com.yscorf.requestlatelimiter.config

import io.lettuce.core.ReadFrom
import io.lettuce.core.RedisClient
import io.lettuce.core.RedisURI
import org.ffm.blogaction.configuration.redis.RedisObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

/**
 * 리액티브 레디스 클라이언트가 필요한게 아니라면 RedisTemplate 사용
 */
@Configuration
class LettuceConfig(
    private val redisProperties: RedisProperties,
) {

    @Bean
    fun redisClient(): RedisClient {
        val redisURI = RedisURI.Builder.redis(redisProperties.host, redisProperties.port)
            .withPassword(redisProperties.password.toCharArray())
            .build()

        return RedisClient.create(redisURI)
    }

    // TCP 통신
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(
            redisProperties.host,
            redisProperties.port
        )

        redisStandaloneConfiguration.password = RedisPassword.of(redisProperties.password)

        val clientConfig = LettuceClientConfiguration.builder()
            .readFrom(ReadFrom.ANY) // replica에서 우선적으로 읽지만 replica에서 읽어오지 못할 경우 Master에서 읽어옴
            .commandTimeout(Duration.ofSeconds(30))
            .build()

        return LettuceConnectionFactory(
            redisStandaloneConfiguration,
            clientConfig
        )
    }
    
    @Bean("stingAnyRedisTemplate")
    fun stingAnyRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        return RedisTemplate<String, Any>().apply {
            this.connectionFactory = redisConnectionFactory()

            this.keySerializer = StringRedisSerializer()
            this.valueSerializer = StringRedisSerializer()

            this.hashKeySerializer = StringRedisSerializer()
            this.hashValueSerializer = StringRedisSerializer()
        }
    }

    // 커넥션 위에서 조작 가능한 메소드 제공
    // 공식 문서에서는 <String, String>으로 되어 있다
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
        return RedisTemplate<Any, Any>().apply {
            setConnectionFactory(connectionFactory)
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer() // JSON 포맷으로 저장
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = GenericJackson2JsonRedisSerializer()

        }
    }

    @Bean
    fun stringJsonRedisTemplate(redisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        val template = StringRedisTemplate()
        template.connectionFactory = redisConnectionFactory

        // 값 직렬화 방식을 JSON으로 설정
        val jsonSerializer = GenericJackson2JsonRedisSerializer(RedisObjectMapper.objectMapper())
        template.valueSerializer = jsonSerializer
        template.hashValueSerializer = jsonSerializer

        // 키 직렬화 방식을 String으로 설정
        template.keySerializer = StringRedisSerializer()
        template.hashKeySerializer = StringRedisSerializer()

        return template
    }

    @Bean
    fun stringLongRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Long> {
        val redisTemplate = RedisTemplate<String, Long>()
        redisTemplate.connectionFactory = connectionFactory
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = GenericToStringSerializer(Long::class.java)

        return redisTemplate
    }

}
