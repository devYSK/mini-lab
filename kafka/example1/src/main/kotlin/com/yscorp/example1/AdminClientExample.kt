package com.yscorp.example1

import org.apache.kafka.clients.admin.AdminClient

class AdminClientExample {
}

fun main() {
    // AdminClient 사용 예시
    // 프로퍼티 먼저
    val props = mapOf<String, Any>(
        "bootstrap.servers" to "localhost:9092"
    )

    // AdminClient 인스턴스 생성
    val adminClient = AdminClient.create(props)

    adminClient.listTopics().names().get().forEach { println(it) }


    adminClient.describeCluster().nodes().get().forEach { println(it) }

    adminClient.createTopics(
        listOf(
            org.apache.kafka.clients.admin.NewTopic("topic1", 3, 2),
            org.apache.kafka.clients.admin.NewTopic("topic2", 3, 2)
        )
    ).all().get()

    adminClient.listTopics().names().get().forEach { println(it) }

    adminClient.deleteTopics(listOf("topic1", "topic2")).all().get()

    adminClient.listTopics().names().get().forEach { println(it) }

    
    adminClient.listConsumerGroups().valid().get().forEach { println(it) }
    val describeCluster = adminClient.describeCluster()

    println(describeCluster.clusterId().get())
    println(describeCluster.controller().get())
    describeCluster.nodes().get().forEach { println(it) }
    adminClient.close()

}