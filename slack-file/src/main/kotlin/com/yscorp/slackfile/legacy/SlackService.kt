package com.yscorp.slackfile.legacy

import com.fasterxml.jackson.databind.ObjectMapper
import com.slack.api.Slack
import com.slack.api.methods.SlackApiException
import com.slack.api.methods.response.chat.ChatPostMessageResponse
import com.yscorp.slackfile.legacy.SlackMessage
import com.yscorp.slackfile.legacy.SlackProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import java.io.IOException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {  }

@Service
class SlackService(
    private val objectMapper: ObjectMapper,
    private val slackProperties: SlackProperties,
) {

    private val slack: Slack = Slack.getInstance()

    fun sendFile() {
        slack.methodsAsync().filesUploadV2 {builder ->
            builder.token(slackProperties.oauthToken)

        }
    }


    fun sendMessage2(channelId: String, slackMessage: SlackMessage) {
        slack.methodsAsync().chatPostMessage { builder ->
            builder.token(slackProperties.oauthToken)

                .text("message")
                .channel(channelId)
                .blocks(slackMessage.blocks())
        }
    }


    fun sendMessage(channelId: String, slackMessage: SlackMessage) {
        try {
            val response: ChatPostMessageResponse = slack.methods()
                .chatPostMessage { builder ->
                    builder.token(slackProperties.oauthToken)
                        .text("message")
                        .channel(channelId)
                        .blocks(slackMessage.blocks())
                }
//            checkResponse(response)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e.message, e)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            throw RuntimeException(e.message, e)
        } catch (e: SlackApiException) {
            e.printStackTrace()
            throw RuntimeException(e.message, e)
        }
    }

    fun sendAsyncMessage(channelId: String, slackMessage: SlackMessage) {
        try {
            val chatPostMessageResponseCompletableFuture: CompletableFuture<ChatPostMessageResponse> =
                slack.methodsAsync()
                    .chatPostMessage { builder ->
                        builder.token(slackProperties.oauthToken)
                            .text("message")
                            .channel(channelId)
                            .blocks(slackMessage.blocks())
                    }
            val response: ChatPostMessageResponse = chatPostMessageResponseCompletableFuture[10, TimeUnit.SECONDS]
//            checkResponse(response)
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException(e.message, e)
        }
    }


    fun sendError(errorChannelId: String, slackMessage: SlackMessage) {
        sendAsyncMessage(errorChannelId, slackMessage)
    }

    fun sendError(e: Throwable) {
        val errorMessage = SlackMessage.error(e)
        sendExceptionMessage(slackMessage = errorMessage)
    }

    fun sendExceptionMessage(slackMessage: SlackMessage) {
        sendAsyncMessage(slackProperties.defaultChannel, slackMessage)
    }


}