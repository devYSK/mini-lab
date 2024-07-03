package com.yscorf.requestlatelimiter

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.BucketConfiguration
import io.github.bucket4j.distributed.ExpirationAfterWriteStrategy
import io.github.bucket4j.distributed.proxy.ClientSideConfig
import io.github.bucket4j.redis.lettuce.cas.LettuceBasedProxyManager
import io.lettuce.core.RedisClient
import io.lettuce.core.codec.ByteArrayCodec
import io.lettuce.core.codec.RedisCodec
import io.lettuce.core.codec.StringCodec
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.function.Supplier


@Component
class APIRateLimiter(redisClient: RedisClient) {

    private val log = LoggerFactory.getLogger(APIRateLimiter::class.java)

    // LettuceBasedProxyManager 객체를 생성, 이 객체는 버킷의 생성 및 관리를 담당
    private val proxyManager: LettuceBasedProxyManager<String>

    // 동일한 API 키에 대한 요청을 처리하기 위해 버킷을 재사용하기 위해 버킷을 저장하는 맵을 생성
    private val buckets: ConcurrentMap<String, Bucket> = ConcurrentHashMap()

    /**
     * APIRateLimiter 생성자.
     *
     * @param redisClient Redis 클라이언트
     */
    init {
        // Redis 연결을 생성
        val connection = redisClient.connect(RedisCodec.of(StringCodec.UTF8, ByteArrayCodec.INSTANCE))

        // ClientSideConfig 설정
        val expirationStrategy = ExpirationAfterWriteStrategy.basedOnTimeForRefillingBucketUpToMax(
            Duration.ofSeconds(100)
        )

        val clientSideConfig = ClientSideConfig.getDefault()
            .withExpirationAfterWriteStrategy(expirationStrategy)

        // Redis 연결을 이용해 LettuceBasedProxyManager 객체를 생성
        proxyManager = LettuceBasedProxyManager.builderFor(connection)
            .withClientSideConfig(clientSideConfig)
            .build()
    }

    /**
     * API 키에 해당하는 버킷을 가져오거나, 없을 경우 새로 생성하는 메서드.
     *
     * @param apiKey API 키
     * @return 해당 API 키에 대응하는 버킷
     */
    private fun getOrCreateBucket(apiKey: String, capacity: Long, duration: Duration): Bucket {

        return buckets.computeIfAbsent(apiKey) { key: String ->
            // 버킷 설정을 생성
            val createBucketConfiguration = createBucketConfiguration(capacity, duration)
            log.info("Creating bucket")

            val configurationSupplier = Supplier { createBucketConfiguration }
            proxyManager.builder().build(key, configurationSupplier)
        }
    }

    /**
     * 버킷 설정을 생성하는 메서드.
     *
     * @return 생성된 버킷 설정
     */
    private fun createBucketConfiguration(capacity: Long, period: Duration): BucketConfiguration {
        val bandwidth = Bandwidth.builder()
            .capacity(capacity)
            .refillIntervally(capacity, period)
            .initialTokens(capacity)
            .id("default123")
            .build()

        return BucketConfiguration.builder() // 버킷에 대한 제한(용량과 재충전 속도)을 설정
            .addLimit(bandwidth)
            .build()
    }

    /**
     * API 키에 해당하는 버킷에서 토큰을 소비하려고 시도하는 메서드.
     *
     * @param apiKey API 키
     * @return 토큰 소비 성공 여부
     */
    fun tryConsume(apiKey: String, capacity: Long, duration: Duration): Boolean {
        // API 키에 해당하는 버킷을 가져옴
        val bucket: Bucket = getOrCreateBucket(apiKey, capacity, duration)
        // 버킷에서 토큰을 소비하려고 시도하고, 그 결과를 반환
        val consumed: Boolean = bucket.tryConsume(1)

        log.info("API Key: {}, Consumed: {}, tokens: ${bucket.availableTokens} Time: {}", apiKey, consumed, LocalDateTime.now())

        return consumed
    }
    fun <T> tryRateLimit(
        apiKey: String,
        capacity: Long,
        duration: Duration,
        action: () -> T?
    ): T? {
        while (true) {
            if (tryConsume(apiKey, capacity, duration)) {
                return action()
            }
            println("delay....")
            Thread.sleep(100)
        }
    }

    suspend fun <T> tryRateLimitSuspend(
        apiKey: String,
        capacity: Long,
        duration: Duration,
        action: suspend () -> T?
    ): T? {
        while (true) {
            if (tryConsume(apiKey, capacity, duration)) {
                return action()
            }
            log.info("delay....")
            delay(300)
        }
    }

    companion object {
        // 버킷의 용량을 설정, 이는 버킷에 담길 수 있는 토큰의 최대 수를 의미
        private const val CAPACITY = 3

        // 토큰이 얼마나 빠르게 재충전될지 설정, 이는 지정된 시간 동안 버킷에 추가될 토큰의 수를 의미
        private const val REFILL_AMOUNT = 3

        // 토큰이 재충전되는 빈도를 설정
        private val REFILL_DURATION: Duration = Duration.ofSeconds(5)
    }
}
