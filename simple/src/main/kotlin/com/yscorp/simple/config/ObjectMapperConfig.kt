package com.yscorp.simple.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


@Configuration
class ObjectMapperConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        return objectMapper
    }
}


val objectMapper: ObjectMapper = ObjectMapper()
    .registerKotlinModule()
    .registerModules(Jdk8Module(), getJavaTimeModule())
    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true)


private const val DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS"

private const val DEFAULT_DATE_FORMAT = "yyyy-MM-dd"

private const val DEFAULT_TIME_FORMAT = "HH:mm:ss"

/**
 * Could not read JSON: Java 8 date/time type `java.time.LocalDate` not supported by default: add
 * Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling 관련 오류 대응
 */
private fun getJavaTimeModule(): JavaTimeModule {
    val module = JavaTimeModule()
    module.addSerializer(
        LocalDateTime::class.java, LocalDateTimeSerializer(
            DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)
        )
    )
    module.addSerializer(
        LocalDate::class.java,
        LocalDateSerializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT))
    )
    module.addSerializer(
        LocalTime::class.java,
        LocalTimeSerializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT))
    )
    module.addDeserializer(
        LocalDateTime::class.java, LocalDateTimeDeserializer(
            DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT)
        )
    )
    module.addDeserializer(
        LocalDate::class.java,
        LocalDateDeserializer(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT))
    )
    module.addDeserializer(
        LocalTime::class.java,
        LocalTimeDeserializer(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT))
    )
    return module
}