package com.ys.redis

import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import org.springframework.boot.test.context.TestConfiguration
import redis.embedded.RedisExecProvider
import redis.embedded.RedisServer
import redis.embedded.util.Architecture
import redis.embedded.util.OS


// Inmemory
@TestConfiguration
class InmemoryTestRedisConfiguration(

    private val redisProperties: RedisProperties) {

    private val redisServer: RedisServer = RedisServer(redisProperties.port)

    @PostConstruct
    fun postConstruct() {
        redisServer.start()
    }

    @PreDestroy
    fun preDestroy() {
        redisServer.stop()
    }

    fun redisServer() {

        // 운영 체제별 Redis 실행 파일의 경로 정의
        val customProvider = RedisExecProvider.defaultProvider()
            .override(OS.UNIX, "/path/unix/redis")
            .override(OS.WINDOWS, Architecture.x86_64, "/path/windows/redis")
            .override(OS.MAC_OS_X, Architecture.x86_64, "/path/macosx/redis")

        // RedisServer 인스턴스 생성
        val redisServer = RedisServer(customProvider, redisProperties.port)
    }
}