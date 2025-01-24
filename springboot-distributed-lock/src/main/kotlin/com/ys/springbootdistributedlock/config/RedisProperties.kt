package com.ys.springbootdistributedlock.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "spring.data.redis")
class RedisProperties @ConstructorBinding constructor(
    val host: String,
    val port: Int,
    val password: String,
) {
}