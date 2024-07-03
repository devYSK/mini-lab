package com.yscorf.requestlatelimiter

import com.yscorf.requestlatelimiter.client.WebClientApi
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
class ApiController(
    private val api: WebClientApi,
    private val apiRateLimiter: APIRateLimiter,
) {

    @GetMapping("/test")
    suspend fun query(@RequestParam("query") query: String): String? {
        return apiRateLimiter.tryRateLimitSuspend("test-api", 10, Duration.ofSeconds(1)) {
            api.get(query)
        }
    }

    @GetMapping("/api/search")
    fun search(@RequestParam("q") query: String): String {
        return "search result for $query"
    }


}
