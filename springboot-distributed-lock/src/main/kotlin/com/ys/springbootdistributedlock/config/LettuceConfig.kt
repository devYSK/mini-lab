package com.ys.springbootdistributedlock.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConfiguration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisPassword
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
class LettuceConfig(
    val redisProperties: RedisProperties

) {
    // TCP 통신
    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration(redisProperties.host, redisProperties.port)

        redisStandaloneConfiguration.password = RedisPassword.of(redisProperties.password)

        return LettuceConnectionFactory(redisStandaloneConfiguration)
    }

//    // Unit 소켓 통신
//    @Bean
//    public LettuceConnectionFactory redisConnectionFactory() {
//        return new LettuceConnectionFactory(new RedisSocketConfiguration("/socket-dir"));
//    }

    //    // Unit 소켓 통신
    //    @Bean
    //    public LettuceConnectionFactory redisConnectionFactory() {
    //        return new LettuceConnectionFactory(new RedisSocketConfiguration("/socket-dir"));
    //    }
    // 커넥션 위에서 조작 가능한 메소드 제공
    // 공식 문서에서는 <String, String>으로 되어 있지만
    // 출력값을 String으로 제한두지 않으려고 <String, Object>로 변경
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
        val redisTemplate = RedisTemplate<Any, Any>()

        redisTemplate.apply {
            keySerializer = StringRedisSerializer()
            valueSerializer = GenericJackson2JsonRedisSerializer() // JSON 포맷으로 저장
        }

        redisTemplate.connectionFactory = connectionFactory

        return redisTemplate
    }

    // 문자열에 특화한 메소드 제공
    @Bean
    fun stringRedisTemplate(redisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        val redisTemplate = StringRedisTemplate()

        redisTemplate.apply {
            connectionFactory = redisConnectionFactory
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
        }

        return redisTemplate
    }

}