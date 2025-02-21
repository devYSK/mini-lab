package com.yscorp.lgtm.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ObjectMapperConfig {

    @Bean
    fun objectMapper(): ObjectMapper {
        return objectMapper
    }

}

val objectMapper = jacksonObjectMapper().also {

    it.registerKotlinModule()
    val javaTimeModule = JavaTimeModule()

    it.registerModule(javaTimeModule)

    // 모르는 property에 대해 무시하고 넘어간다.
    it.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)


    it.registerModule(
        KotlinModule.Builder()
            .withReflectionCacheSize(512)
            .configure(KotlinFeature.NullToEmptyCollection, false)
            .configure(KotlinFeature.NullToEmptyMap, false)
            .configure(KotlinFeature.NullIsSameAsDefault, false)
            .configure(KotlinFeature.SingletonSupport, false)
            .configure(KotlinFeature.StrictNullChecks, false)
            .build()
    )

    // 시간 관련 객체(LocalDateTime, java.util.Date)를 직렬화 할 때 timestamp 숫자값이 아닌 포맷팅 문자열로 한다.
    it.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // 문자열로 내리기.

    // Date/Time 관련된 추가 설정 (필요 시)
//    it.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, true)
//    it.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false)
}

fun ObjectMapper.pretty(any: Any?) : String {
    if (any == null) {
        return ""
    }

    return this.writerWithDefaultPrettyPrinter().writeValueAsString(any)
}