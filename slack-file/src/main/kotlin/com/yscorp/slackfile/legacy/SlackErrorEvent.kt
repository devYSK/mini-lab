package com.yscorp.slackfile.legacy

data class SlackErrorEvent(
    val channelId: String,
    val message: SlackMessage,
) {

    companion object {
        fun defaultChannel(
            e: Throwable,
            requestUri: String? = null,
            method: String? = null,
            message: String? = null,
        ) = SlackErrorEvent(DEFAULT_ERROR_CHANNEL, SlackMessage.error(e, requestUri, method, message))

        fun defaultChannel(message: SlackMessage) = SlackErrorEvent(DEFAULT_ERROR_CHANNEL, message)

        fun defaultChannel(message: String) = SlackErrorEvent(
            DEFAULT_ERROR_CHANNEL,
            SlackMessage.message(message = message)
        )

    }

    data class MessageEvent(
        val exception: Throwable,
    )

    data class Message(
        val message: String,
    )

    data class SimpleErrorEvent(
        val channelId: String,
        val message: SlackMessage,
    )

}