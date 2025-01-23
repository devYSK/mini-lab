package com.ys.redis.streams

import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.stream.StreamListener
import org.springframework.stereotype.Component


@Component
class PaymentEventStreamListener : StreamListener<String, MapRecord<String, String, String>> {
    override fun onMessage(message: MapRecord<String, String, String>) {
        val map: Map<*, *> = message.value
        val userId = map["userId"] as String
        val paymentProcessId = map["paymentProcessId"] as String

        // 결제 완료 건에 대해 SMS 발송 처리
        println("[Payment consumed] usrId: $userId   paymentProcessId: $paymentProcessId")
    }
}

