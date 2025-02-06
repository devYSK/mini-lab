package com.ys.lab.chatgpt.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "open-ai")
data class ChatGptProperties(
    private val apiKey: String
) {

    fun firstKey(): String {
        return apiKey
    }

}
