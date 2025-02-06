package com.ys.lab.chatgpt.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.util.function.Consumer

@Configuration
class ChatGptWebClientConfig {
    private val log = LoggerFactory.getLogger(this::class.java)

    @Bean(name = ["openaiWebClient"])
    fun openaiWebClient(): WebClient {

        return WebClient.builder()
            .baseUrl(CHAT_GPT_BASE_URL)
            .filter(logRequest(log))
            .build()
    }

    companion object {
        const val CHAT_GPT_BASE_URL = "https://api.openai.com"

        // 요청 로깅
        fun logRequest(log: Logger): ExchangeFilterFunction {
            return ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
                if (clientRequest.body() is BodyInserters.MultipartInserter) {
                    log.info("Request: {} {} - Multipart content", clientRequest.method(), clientRequest.url())
                    return@ofRequestProcessor Mono.just(clientRequest)
                }

                log.info("Request: {} {}", clientRequest.method(), clientRequest.url())
                log.info("Request Headers: {}", clientRequest.headers())

                Mono.just(clientRequest)
            }
        }

        // 응답 로깅
        fun logResponse(log: Logger): ExchangeFilterFunction {
            return ExchangeFilterFunction.ofResponseProcessor { clientResponse: ClientResponse ->
                log.info("Response Status {}", clientResponse.statusCode())
                clientResponse.headers().asHttpHeaders()
                    .forEach { name: String?, values: List<String?> ->
                        values.forEach(
                            Consumer<String?> { value: String? ->
                                log.info(
                                    "{}={}",
                                    name,
                                    value
                                )
                            })
                    }
                Mono.just(clientResponse)
            }
        }
    }


}
