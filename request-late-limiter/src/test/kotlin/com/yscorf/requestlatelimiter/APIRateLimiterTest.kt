package com.yscorf.requestlatelimiter

import io.lettuce.core.RedisClient
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.slf4j.LoggerFactory


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class APIRateLimiterTest {

    private val log = LoggerFactory.getLogger(APIRateLimiterTest::class.java)

    private lateinit var redisClient: RedisClient
    private lateinit var apiRateLimiter1: APIRateLimiter
    private lateinit var apiRateLimiter2: APIRateLimiter

    @BeforeAll
    fun setup() {
        redisClient = RedisClient.create("redis://localhost:6379")

        // 두 개의 APIRateLimiter 인스턴스를 생성하여 여러 애플리케이션 인스턴스처럼 테스트
        apiRateLimiter1 = APIRateLimiter(redisClient)
        apiRateLimiter2 = APIRateLimiter(redisClient)
    }

    @AfterAll
    fun tearDown() {
        redisClient.shutdown()
    }

    @Test
    @Order(1)
    fun `토큰이 있을 때 tryConsume은 true를 반환해야 한다`() {
        val apiKey = "test-key"
        val capacity = 5L
        val duration = Duration.ofSeconds(1)

        val result = apiRateLimiter1.tryConsume(apiKey, capacity, duration)
        Assertions.assertTrue(result, "토큰이 있을 때 tryConsume은 true를 반환해야 한다")
    }

    @Test
    @Order(2)
    fun `토큰이 없을 때 tryConsume은 false를 반환해야 한다`() {
        val apiKey = "test-key"
        val capacity = 1L
        val duration = Duration.ofSeconds(1)

        // 첫 번째 인스턴스에서 토큰을 모두 소모함
        apiRateLimiter1.tryConsume(apiKey, capacity, duration)

        // 두 번째 인스턴스에서 토큰을 소모하려고 시도함
        val result = apiRateLimiter2.tryConsume(apiKey, capacity, duration)
        Assertions.assertFalse(result, "토큰이 없을 때 tryConsume은 false를 반환해야 한다")
    }

    @Test
    @Order(3)
    fun `여러 인스턴스에서 동작하는 레이트 리미터를 테스트`() {
        val apiKey = "test-key-global"
        val capacity = 3L
        val duration = Duration.ofSeconds(5)

        // 첫 번째 인스턴스에서 토큰 소비
        val result1 = apiRateLimiter1.tryConsume(apiKey, capacity, duration)
        Assertions.assertTrue(result1, "첫 번째 인스턴스에서 토큰을 소비할 수 있어야 한다")

        // 두 번째 인스턴스에서 토큰 소비
        val result2 = apiRateLimiter2.tryConsume(apiKey, capacity, duration)
        Assertions.assertTrue(result2, "두 번째 인스턴스에서 토큰을 소비할 수 있어야 한다")

        // 두 번째 인스턴스에서 다시 토큰 소비 (토큰 소진됨)
        val result3 = apiRateLimiter2.tryConsume(apiKey, capacity, duration)
        Assertions.assertFalse(result3, "두 번째 인스턴스에서 토큰을 더 이상 소비할 수 없어야 한다")
    }
}
