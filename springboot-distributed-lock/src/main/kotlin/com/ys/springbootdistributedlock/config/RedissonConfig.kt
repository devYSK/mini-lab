package com.ys.springbootdistributedlock.config

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.client.codec.Codec
import org.redisson.client.codec.StringCodec
import org.redisson.config.Config
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RedissonConfig(
    private val redisProperties: RedisProperties,
) {

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()
        val codec: Codec = StringCodec() // redis-cli에서 보기 위해

        config.codec = codec

        config.useSingleServer().apply {
            //https로 접근시에는 rediss://로 접근해야 한다.
            address = "$REDISSON_HOST_PREFIX${redisProperties.host}:${redisProperties.port}"
            password = redisProperties.password
            isSslEnableEndpointIdentification = false
            timeout = 3000
        }

        return Redisson.create(config)
    }

    companion object {
        const val REDISSON_LOCK_PREFIX = "LOCK:"
        const val REDISSON_HOST_PREFIX = "redis://"
    }

}