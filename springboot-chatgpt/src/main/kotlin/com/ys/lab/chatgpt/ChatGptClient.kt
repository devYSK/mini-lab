package com.ys.lab.chatgpt

import com.fasterxml.jackson.databind.ObjectMapper
import com.ys.lab.chatgpt.config.ChatGptWebClientConfig
import com.ys.lab.chatgpt.config.ChatGptWebClientConfig.Companion.CHAT_GPT_BASE_URL
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToFlux
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.IOException
import java.time.Duration

/**
 * api key 발급: https://platform.openai.com/account/api-keys
 * see : https://platform.openai.com/docs/guides/gpt
 *
 * https://platform.openai.com/docs/api-reference/chat/create
 */
@Component
class ChatGptClient(
    @Qualifier("openaiWebClient") private val webClient: WebClient,

    private val objectMapper: ObjectMapper
) {

    companion object {
        const val BASE_ENDPOINT = "/v1/chat/completions"
    }

    private val log = LoggerFactory.getLogger(this::class.java)

    private val fluxClient: WebClient = WebClient.builder()
        .baseUrl(CHAT_GPT_BASE_URL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .filter(ChatGptWebClientConfig.logRequest(log))
        .filter(ChatGptWebClientConfig.logResponse(log))
        .build()

    suspend fun query(prompt: String, apiKey: String, model: GptModel = GptModel.GPT_4): ChatResponse {
        val request = ChatRequest.of(model, prompt)

        log.info(
            """
            request data : 
            ${objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request)} 
            """.trimIndent()
        )
        return webClient.post()
            .uri(BASE_ENDPOINT) // just the endpoint path
            .header("Authorization", "Bearer $apiKey")
            .bodyValue(request)
            .retrieve()
            .onStatus({ status -> !status.is2xxSuccessful }) { response ->
                response.bodyToMono<String>().flatMap { body ->
                    Mono.error(RuntimeException("Error: $body"))
                }
            }
            .bodyToMono<ChatResponse>()
            .timeout(Duration.ofSeconds(300))
            .awaitSingle()
    }

    suspend fun queryToString(prompt: String, apiKey: String, model: GptModel = GptModel.GPT_4): String {
        val request = ChatRequest.of(model, prompt)

        log.info(
            """
            request data : 
            ${objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request)} 
            """.trimIndent()
        )

        return webClient.post()
            .uri(BASE_ENDPOINT) // just the endpoint path
            .header("Authorization", "Bearer $apiKey")
            .bodyValue(request)
            .retrieve()
            .onStatus({ status -> !status.is2xxSuccessful }) { response ->
                response.bodyToMono<String>().flatMap { body ->
                    Mono.error(RuntimeException("Error: $body"))
                }
            }
            .bodyToMono<ChatResponse>()
            .timeout(Duration.ofSeconds(300))
            .awaitSingle()
            .choices
            .first()
            .message
            .content
    }

    fun queryStream(prompt: String, apiKey: String): Flux<ChatStreamResponse> {
        val request = ChatRequest.stream(GptModel.GPT_3_5_TURBO, prompt)

        log.info(
            """
            request data : 
            ${objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request)} 
            """.trimIndent()
        )

        return fluxClient.post()
            .uri(BASE_ENDPOINT) // just the endpoint path
            .header("Authorization", "Bearer $apiKey")
            .bodyValue(request)
            .retrieve()
            .onStatus({ status -> !status.is2xxSuccessful }) { response ->
                response.bodyToMono<String>().flatMap { body ->
                    Mono.error(RuntimeException("Error: $body"))
                }
            }
            .bodyToFlux<String>()
            .map { jsonString ->
                objectMapper.readValue(jsonString, ChatStreamResponse::class.java)
            }
            .takeWhile { response ->
                // finish_reason이 null이 아니고 "stop"이면 스트림을 끝냅니다.
                !(response.choices.firstOrNull()?.finishReason?.equals("stop") ?: false)
            }
            .doOnComplete {
                log.info("Stream completed.")
            }
    }

    suspend fun queryStreamCoroutine(prompt: String, apiKey: String): List<ChatStreamResponse> {
        val request = ChatRequest.stream(GptModel.GPT_3_5_TURBO, prompt)

        log.info(
            """
        request data : 
        ${objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request)} 
        """.trimIndent()
        )

        // Flux 대신 리스트를 반환
        return fluxClient.post()
            .uri(BASE_ENDPOINT)
            .header("Authorization", "Bearer $apiKey")
            .bodyValue(request)
            .retrieve()
            .onStatus({ status -> !status.is2xxSuccessful }) { response ->
                response.bodyToMono<String>().flatMap { body ->
                    Mono.error(RuntimeException("Error: $body"))
                }
            }
            .bodyToFlux<String>()
            .map { jsonString ->
                objectMapper.readValue(jsonString, ChatStreamResponse::class.java)
            }
            .takeWhile { response ->
                !(response.choices.firstOrNull()?.finishReason?.equals("stop") ?: false)
            }
            .collectList() // Flux를 리스트로 변환
            .awaitSingle() // 리스트를 단일 값으로 기다림
    }

    fun queryStreamEmitter(prompt: String, apiKey: String, sseEmitter: SseEmitter) {
        val request = ChatRequest.stream(GptModel.GPT_3_5_TURBO, prompt)

        val milliseconds = 5 * 60 * 1000 // 300 밀리세컨드

        log.info(
            """
            request data : 
            ${objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(request)} 
            """.trimIndent()
        )

        fluxClient.post()
            .uri(BASE_ENDPOINT) // just the endpoint path
            .header("Authorization", "Bearer $apiKey")
            .body(BodyInserters.fromValue<Any>(request))
            .exchangeToFlux { response: ClientResponse ->
                response.bodyToFlux(
                    String::class.java
                )
            }
            .doOnNext { line: String ->
                try {
                    if (line == "[DONE]") {
                        sseEmitter.complete()
                        return@doOnNext
                    }
                    sseEmitter.send(SseEmitter.event().data(line))
                } catch (e: IOException) {
                    throw RuntimeException(e)
                }
            }
            .doOnError { ex: Throwable? -> sseEmitter.completeWithError(ex!!) }
            .doOnComplete { sseEmitter.complete() }
            .subscribe()
    }

}

// 로그 스트림의 마지막
// {"id":"chatcmpl-8H4H8G8Ke40tMMppaEqFy1AWhPZse","object":"chat.completion.chunk","created":1699078702,"model":"gpt-3.5-turbo-0613","choices":[{"index":0,"delta":{"content":"!"},"finish_reason":null}]}
// {"id":"chatcmpl-8H4H8G8Ke40tMMppaEqFy1AWhPZse","object":"chat.completion.chunk","created":1699078702,"model":"gpt-3.5-turbo-0613","choices":[{"index":0,"delta":{"content":" :)"},"finish_reason":null}]}
// {"id":"chatcmpl-8H4H8G8Ke40tMMppaEqFy1AWhPZse","object":"chat.completion.chunk","created":1699078702,"model":"gpt-3.5-turbo-0613","choices":[{"index":0,"delta":{},"finish_reason":"stop"}]}
// [DONE]
