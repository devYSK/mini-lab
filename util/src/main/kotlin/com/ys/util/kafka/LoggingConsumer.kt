package com.ys.util.kafka

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.util.*


@Component
class LoggingConsumer {

    @Bean
    fun initConsumer(
        @Value("\${kafka.clusters.bootstrapservers}") bootstrapServers: String,
        @Value("\${logging.topic}") topic: String,
    ): KafkaConsumer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = bootstrapServers
        props["group.id"] = "my-group"
        props["key.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer"
        props["value.deserializer"] = "org.apache.kafka.common.serialization.StringDeserializer"
        val kafkaConsumer  = KafkaConsumer<String, String>(props)

        kafkaConsumer.subscribe(listOf(topic))

        return kafkaConsumer
    }
}