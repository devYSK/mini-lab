package com.yscorp.example1

import io.confluent.common.utils.Utils
import org.apache.kafka.clients.producer.Partitioner
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.Cluster
import org.apache.kafka.common.InvalidRecordException
import org.apache.kafka.common.PartitionInfo
import kotlin.math.abs

class BananaPartitioner : Partitioner {

    override fun configure(configs: Map<String, *>) {
        // 설정 관련 처리 필요 시 구현
    }

    override fun partition(
        topic: String,
        key: Any?,
        keyBytes: ByteArray?,
        value: Any?,
        valueBytes: ByteArray?,
        cluster: Cluster
    ): Int {
        val partitions: List<PartitionInfo> = cluster.partitionsForTopic(topic)
        val numPartitions = partitions.size

        if (keyBytes == null || key !is String) {
            throw InvalidRecordException("We expect all messages to have customer name as key")
        }

        return if (key == "Banana") {
            numPartitions - 1 // Banana는 항상 마지막 파티션으로 보냄
        } else {
            // 다른 레코드는 나머지 파티션으로 해시 처리
            abs(Utils.murmur2(keyBytes) % (numPartitions - 1))
        }
    }

    override fun close() {
        // 리소스 정리 필요 시 구현
    }
}

fun main() {
    val record = ProducerRecord<String, String>("demo", "Banana", "Banana")

    record.headers().add("headerKey", "headerValue".toByteArray())
}