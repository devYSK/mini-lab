package com.yscorp.webflux.config.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
class JwtProperties(
    val secretKey: String,

    val accessTokenExpirationSecond: Long,

    val refreshTokenExpirationSecond: Long,

    val refreshSecretKey: String,
) {

}
