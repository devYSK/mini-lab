package com.ys.redis.pubsubmessage

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service


@Service
class MessageListenService : MessageListener {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun onMessage(message: Message, pattern: ByteArray?) {
        log.info("Received {} channel: {}", String(message.channel), String(message.body))
    }
}

