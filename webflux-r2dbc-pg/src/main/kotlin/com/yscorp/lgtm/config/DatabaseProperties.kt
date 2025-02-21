package com.yscorp.webflux.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.config.r2dbc")
data class R2dbcProperties(
    val write: WriteProperties,
    val read: ReadProperties
)

data class WriteProperties(
    val url: String,
    val username: String,
    val password: String
)

data class ReadProperties(
    val replicas: List<ReplicaProperties>
)

data class ReplicaProperties(
    val url: String,
    val username: String,
    val password: String
)
