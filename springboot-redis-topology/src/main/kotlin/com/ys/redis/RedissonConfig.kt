package com.ys.redis

@Configuration
class RedissonConfig {


    companion object {
        private const val REDISSON_HOST_PREFIX = "redis://"
        private const val REDISSON_LOCK_PREFIX = "LOCK:"
    }

    private val log: Logger get() = LoggerFactory.getLogger(this.javaClass)

    @Bean
    fun redissonClient(): RedissonClient {
        val config = Config()

        config.useSingleServer().apply {
            address = "$REDISSON_HOST_PREFIX${redissonProperties.host}:${redissonProperties.port}"
            password = redissonProperties.password
            isSslEnableEndpointIdentification = false
            timeout = 3000
        }

        return Redisson.create(config)
    }
}