package com.yscorp.example1

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

import customerManagement.avro.Customer

class AvroProducer {
}

fun produce() {

    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer")
        put("schema.registry.url", "http://localhost:8081")
    }
    // generate
    val customer = Customer.newBuilder()
        .setId(1)
        .setName("John")
        .build()

    KafkaProducer<String, Customer>(props).use { producer ->
        val record = ProducerRecord<String, Customer>("demo", "key", customer)
        producer.send(record) { metadata, exception ->
            if (exception == null) {
                println("메시지 전송 성공  : ${metadata.serializedValueSize()}")
                println("key : \"key\", value : \"value$\"")
            } else {
                println("메시지 전송 실패 : ${exception.message}")
            }
        }
    }
}

fun consume() {
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "io.confluent.kafka.serializers.KafkaAvroSerializer")
        put("schema.registry.url", "http://localhost:8081")
    }
    KafkaProducer<String, Customer>(props).use { producer ->
        val record = ProducerRecord<String, Customer>("demo", "key", Customer.newBuilder().setId(1).setName("John").build())
        val metadata = producer.send(record).get()
        println("메시지 전송 성공 : ${metadata.serializedValueSize()}")
        println("key : \"key\", value : \"value\"")
    }
}