package com.yscorp.example1

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.stream.Collectors

class SeekConsumer {
}


fun main() {
    val props = Properties().apply {
        put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        put(ConsumerConfig.GROUP_ID_CONFIG, "test")
        put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
        put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
        put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")
    }

    val oneHourEariler = Instant.now().atZone(ZoneId.of("Asia/Seoul")).minusHours(1).toEpochSecond()


    KafkaConsumer<String, String>(props).use { consumer ->
        consumer.subscribe(listOf("demo"))

        while (true) {
            val partitionTimestampMap = consumer.assignment().stream()
                .collect(Collectors.toMap({ tp -> tp }, { tp -> oneHourEariler }))

            val offsetMap = consumer.offsetsForTimes(partitionTimestampMap)

            offsetMap.forEach { (tp, offset) ->
                consumer.seek(tp, offset.offset())
            }
        }
    }
}