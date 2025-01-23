package com.ys.util.kafka

import com.ys.util.LoggingProducer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*


@Component
class KafkaLoggingProducer(
    @Value("\${kafka.clusters.bootstrapservers}") bootstrapServers: String,
    @Value("\${logging.topic}") private val topic: String,
    private var producer: KafkaProducer<String, String>
) : LoggingProducer {

    init {
        // Producer Initialization ';'
        val props = Properties()

        // kafka:29092
        props["bootstrap.servers"] = bootstrapServers

        // "key:value"
        props["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        props["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        producer = KafkaProducer(props)
    }

    // Kafka Cluster [key, value] Produce
    override fun sendMessage(key: String, value: String) {
        val record: ProducerRecord<String, String> = ProducerRecord(topic, key, value)
        producer.send(record) { metadata, exception ->
            if (exception == null) {
                // System.out.println("Message sent successfully. Offset: " + metadata.offset());
            } else {
                exception.printStackTrace()
                // System.err.println("Failed to send message: " + exception.getMessage());
            }
        }
    }
}