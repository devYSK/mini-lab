package com.ys.lab.chatgpt

import com.fasterxml.jackson.annotation.JsonProperty

data class ChatStreamResponse (
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>
) {

    data class Choice(
        val index: Int,
        val delta: Delta,
        @JsonProperty("finish_reason")
        val finishReason: String?
    )

    data class Delta(
        val role: String?,
        val content: String?
    )
}