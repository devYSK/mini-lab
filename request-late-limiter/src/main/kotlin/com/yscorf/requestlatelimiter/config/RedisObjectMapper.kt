package org.ffm.blogaction.configuration.redis

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule

object RedisObjectMapper {
    private val objectMapper: ObjectMapper

    init {
        this.objectMapper = init()
    }

    private fun init(): ObjectMapper {
        val objectMapper = ObjectMapper()

        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

        objectMapper.registerModules(
            KotlinModule.Builder()
                .withReflectionCacheSize(512)
                .configure(KotlinFeature.NullToEmptyCollection, false)
                .configure(KotlinFeature.NullToEmptyMap, false)
                .configure(KotlinFeature.NullIsSameAsDefault, false)
                .configure(KotlinFeature.SingletonSupport, true)
                .configure(KotlinFeature.StrictNullChecks, false)
                .build()
        )

        objectMapper.registerModule(JavaTimeModule())
        objectMapper.activateDefaultTyping(
            BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Any::class.java).build(), ObjectMapper.DefaultTyping.EVERYTHING)

        return objectMapper
    }

    fun objectMapper(): ObjectMapper {
        return this.objectMapper
    }

    fun write(any: Any) : String {
        return objectMapper.writeValueAsString(any)
    }

    fun <T> readValue(value: String, valueType: Class<T>): T {
        return objectMapper.readValue(value, valueType)
    }

}