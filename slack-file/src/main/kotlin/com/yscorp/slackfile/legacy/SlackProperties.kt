package com.yscorp.slackfile.legacy

import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties

lateinit var DEFAULT_ERROR_CHANNEL: String

@ConfigurationProperties(prefix = "slack")
data class SlackProperties(
    val oauthToken: String,
    val defaultChannel: String,
) {

    @PostConstruct
    fun init() {
        DEFAULT_ERROR_CHANNEL = defaultChannel
    }

}