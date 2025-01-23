package com.ys.redis.cachemanager

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import java.time.Duration

/**
 * @
 */
@EnableCaching
@Configuration
class RedisCacheManagerConfig(
    private val cacheProperties: CacheProperties,
) {

    // see : application.yml
    @ConfigurationProperties(prefix = "cache")
    data class CacheProperties(
        val properties: List<CacheProperty>,
    ) {
        data class CacheProperty(
            val name: String,
            val ttl: Int,
        )
    }

    /**
     * Spring Boot 가 기본적으로 RedisCacheManager 를 자동 설정해줘서 RedisCacheConfiguration 없어도 사용 가능
     * Bean 을 새로 선언하면 직접 설정한 RedisCacheConfiguration 이 적용됨
     *  Redis 는 직렬화/역직렬화 때문에 별도의 캐시 설정이 필요하고 이 때 사용하는게 RedisCacheConfiguration 입니다.
     *
     * RedisCacheConfiguration 설정은 Redis 기본 설정을 오버라이드 한다고 생각하면 됩니다.
     *
     * computePrefixWith: Cache Key prefix 설정
     * entryTtl: 캐시 만료 시간
     * disableCachingNullValues: 캐싱할 때 null 값을 허용하지 않음 (#result == null 과 함께 사용해야 함)
     * serializeKeysWith: Key 를 직렬화할 때 사용하는 규칙. 보통은 String 형태로 저장
     * serializeValuesWith: Value 를 직렬화할 때 사용하는 규칙. Jackson2 를 많이 사용함
     */
    @Bean
    fun redisCacheConfiguration(): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofSeconds(60))
            .disableCachingNullValues()
            .serializeKeysWith(
                RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
            )
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(GenericJackson2JsonRedisSerializer())
            )
    }


    // 여러 Redis Cache 에 관한 설정을 하고 싶다면 RedisCacheManagerBuilderCustomizer 를 사용할 수 있다.
    @Bean
    fun redisCacheManagerBuilderCustomizer(): RedisCacheManagerBuilderCustomizer {
        val ptv: PolymorphicTypeValidator = BasicPolymorphicTypeValidator.builder()
            .allowIfSubType(Object::class.java)
            .build()

        val objectMapper = ObjectMapper().apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            registerModule(JavaTimeModule())
            activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL)
            disable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS)
        }

        return RedisCacheManagerBuilderCustomizer { builder ->
            cacheProperties.properties.forEach { property ->
                builder.withCacheConfiguration(
                    property.name, RedisCacheConfiguration.defaultCacheConfig()
                        .disableCachingNullValues()
                        .serializeKeysWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(
                                StringRedisSerializer()
                            )
                        )
                        .serializeValuesWith(
                            RedisSerializationContext.SerializationPair.fromSerializer(
                                GenericJackson2JsonRedisSerializer(objectMapper)
                            )
                        )
                        .entryTtl(Duration.ofSeconds(property.ttl.toLong()))
                )
            }
        }
    }

    // 아래처럼도 가능
//    @Bean
//    fun redisCacheManagerBuilderCustomizer2(): RedisCacheManagerBuilderCustomizer? {
//        return RedisCacheManagerBuilderCustomizer { builder: RedisCacheManagerBuilder ->
//            builder
//                .withCacheConfiguration(
//                    "cache1",
//                    RedisCacheConfiguration.defaultCacheConfig()
//                        .computePrefixWith { cacheName: String -> "prefix::$cacheName::" }
//                        .entryTtl(Duration.ofSeconds(120))
//                        .disableCachingNullValues()
//                        .serializeKeysWith(
//                            RedisSerializationContext.SerializationPair.fromSerializer(StringRedisSerializer())
//                        )
//                        .serializeValuesWith(
//                            RedisSerializationContext.SerializationPair.fromSerializer(
//                                GenericJackson2JsonRedisSerializer()
//                            )
//                        )
//                )
//                .withCacheConfiguration(
//                    "cache2",
//                    RedisCacheConfiguration.defaultCacheConfig()
//                        .entryTtl(Duration.ofHours(2))
//                        .disableCachingNullValues()
//                )
//        }
//    }

}