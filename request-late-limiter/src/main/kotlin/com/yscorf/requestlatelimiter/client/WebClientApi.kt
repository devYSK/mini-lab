package com.yscorf.requestlatelimiter.client

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class WebClientApi {



    private val client =  WebClient.builder()
        .baseUrl("http://localhost:8080")
        .build()


    suspend fun get(query: String): String? {
        return client.get()
            .uri("/api/search?q=$query")
            .retrieve()
            .bodyToMono(String::class.java)
            .awaitSingle()
    }

}
