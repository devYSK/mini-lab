package com.ys.redis

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.test.context.ContextConfiguration
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@ContextConfiguration(initializers = [RedisTestContainerIntegrationTest.IntegrationTestInitializer::class])
@Testcontainers
class RedisTestContainerIntegrationTest {

    companion object {
        // Redis 컨테이너 설정
        @Container
        val redis = GenericContainer<Nothing>(DockerImageName.parse("redis:6")).apply {
            withExposedPorts(6379)
            start()
        }

    }
    // ApplicationContextInitializer 구현을 통해 테스트 속성 설정
    class IntegrationTestInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            val redisHost = redis.host
            val redisPort = redis.firstMappedPort

            val properties = mapOf(
                "spring.data.redis.host" to redisHost,
                "spring.data.redis.port" to redisPort.toString()
            )

            TestPropertyValues.of(properties).applyTo(applicationContext)
        }
    }
}