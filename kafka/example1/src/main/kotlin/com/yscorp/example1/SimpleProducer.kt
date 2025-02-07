package com.yscorp.example1

import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*

class SimpleProducer {

}

fun main() {
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE)
        put(ProducerConfig.ACKS_CONFIG, "all")
    }

    KafkaProducer<String, String>(props).use { producer ->
        repeat(5) {
            val record = ProducerRecord<String, String>("demo", "key$it", "value$it")
            producer.send(record) { metadata, exception ->
                if (exception == null) {
                    println("메시지 전송 성공 ${it} : ${metadata.serializedValueSize()}")
                    println("key : \"key$it\", value : \"value$it\"")
                } else {
                    println("메시지 전송 실패 : ${exception.message}")
                }
            }
        }
    }
}

fun sendSync() {
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE)
        put(ProducerConfig.ACKS_CONFIG, "all")
    }

    KafkaProducer<String, String>(props).use { producer ->
        val record = ProducerRecord<String, String>("demo", "key", "value")
        val metadata = producer.send(record).get()
        println("메시지 전송 성공 : ${metadata.serializedValueSize()}")
        println("key : \"key\", value : \"value\"")
    }
}

fun sendAsync() {
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE)
        put(ProducerConfig.ACKS_CONFIG, "all")
    }

    KafkaProducer<String, String>(props).use { producer ->
        val record = ProducerRecord<String, String>("demo", "key", "value")
        producer.send(record) { metadata, exception ->
            println("callback")
            if (exception == null) {
                println("메시지 전송 성공 : ${metadata.serializedValueSize()}")
                println("key : \"key\", value : \"value\"")
            } else {
                println("메시지 전송 실패 : ${exception.message}")
            }
        }
    }
}

class DemoCallback : Callback {
    override fun onCompletion(metadata: org.apache.kafka.clients.producer.RecordMetadata?, exception: Exception?) {
        if (exception == null) {
            println("메시지 전송 성공 : ${metadata?.serializedValueSize()}")
            println("key : \"key\", value : \"value\"")
        } else {
            println("메시지 전송 실패 : ${exception.message}")
        }
    }
}

fun sendAsyncWithCallback() {
    val props = Properties().apply {
        put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
        put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE)
        put(ProducerConfig.ACKS_CONFIG, "all")
    }

    KafkaProducer<String, String>(props).use { producer ->
        val record = ProducerRecord<String, String>("demo", "key", "value")
        producer.send(record, DemoCallback())
    }
}