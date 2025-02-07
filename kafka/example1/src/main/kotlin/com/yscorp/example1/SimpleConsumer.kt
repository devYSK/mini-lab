package com.yscorp.example1

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.clients.consumer.OffsetCommitCallback
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

class SimpleConsumer {
}

fun main() {
    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092, localhost:9093")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.GROUP_ID_CONFIG, "demo-group")
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") // 가장 오래된 메시지부터 읽기 시작
    }

    KafkaConsumer<String, String>(props).use { consumer ->
        consumer.subscribe(listOf("demo")) // "demo" 토픽 구독

        while (true) {
            val records = consumer.poll(Duration.ofMillis(100)) // 메시지 가져오기
            for (record in records) {
                println("토픽: ${record.topic()}, 파티션: ${record.partition()}, 오프셋: ${record.offset()}")
                println("key: ${record.key()}, value: ${record.value()}")
            }
        }
    }
}

fun commitAsync() {
    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092, localhost:9093")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.GROUP_ID_CONFIG, "demo-group")
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") // 가장 오래된 메시지부터 읽기 시작
        put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false") // 자동 커밋 비활성화
    }

    KafkaConsumer<String, String>(props).use { consumer ->
        consumer.subscribe(listOf("demo")) // "demo" 토픽 구독

        while (true) {
            val records = consumer.poll(Duration.ofMillis(100)) // 메시지 가져오기
            for (record in records) {
                println("토픽: ${record.topic()}, 파티션: ${record.partition()}, 오프셋: ${record.offset()}")
                println("key: ${record.key()}, value: ${record.value()}")
            }
            consumer.commitAsync { offsets, exception ->
                if (exception == null) {
                    println("오프셋 커밋 성공")
                } else {
                    println("오프셋 커밋 실패 : ${exception.message} ,${offsets}")
                }
            } // 오프셋 커밋
        }
    }
}

fun commitSyncAndAsync() {
    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092, localhost:9093")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.GROUP_ID_CONFIG, "demo-group")
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") // 가장 오래된 메시지부터 읽기 시작
        put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false") // 자동 커밋 비활성화
    }

    val closing = false;

    KafkaConsumer<String, String>(props).use { consumer ->
        consumer.subscribe(listOf("demo")) // "demo" 토픽 구독

        while (!closing) {
            val records = consumer.poll(Duration.ofMillis(100)) // 메시지 가져오기
            for (record in records) {
                println("토픽: ${record.topic()}, 파티션: ${record.partition()}, 오프셋: ${record.offset()}")
                println("key: ${record.key()}, value: ${record.value()}")
            }
            consumer.commitAsync()
        }
        consumer.commitSync() // 오프셋 커밋
    }
}

fun rebalanceListener() {
    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092, localhost:9093")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer::class.java.name)
        put(ConsumerConfig.GROUP_ID_CONFIG, "demo-group")
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest") // 가장 오래된 메시지부터 읽기 시작
        put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false") // 자동 커밋 비활성화
    }

    val currentOffsets : MutableMap<TopicPartition, OffsetAndMetadata> = mutableMapOf()

    KafkaConsumer<String, String>(props).use { consumer ->

        consumer.subscribe(listOf("demo"), object : ConsumerRebalanceListener {
            override fun onPartitionsRevoked(partitions: Collection<TopicPartition>) {
                println("리밸런스 발생 전 호출")
                consumer.commitSync(currentOffsets) // 오프셋 커밋
            }

            override fun onPartitionsAssigned(partitions: Collection<TopicPartition>) {
                println("리밸런스 발생 후 호출")
            }
        }) // "demo" 토픽 구독

        while (true) {
            val records = consumer.poll(Duration.ofMillis(100)) // 메시지 가져오기
            for (record in records) {
                println("토픽: ${record.topic()}, 파티션: ${record.partition()}, 오프셋: ${record.offset()}")
                println("key: ${record.key()}, value: ${record.value()}")

                currentOffsets[TopicPartition(record.topic(), record.partition())]= OffsetAndMetadata(record.offset() + 1, null)
            }
            consumer.commitAsync(currentOffsets, null)
        }
    }
}