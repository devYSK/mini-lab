package com.ys.redis.pubsubchat

import org.springframework.context.annotation.Bean
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.stereotype.Service
import java.util.*


@Service
class ChatService(
    private val container: RedisMessageListenerContainer,
    var redisTemplate: RedisTemplate<String, String>,
) : MessageListener {

    fun enterCharRoom(chatRoomName: String?) {

        container.addMessageListener(this, ChannelTopic(chatRoomName!!))

        val `in` = Scanner(System.`in`)

        while (`in`.hasNextLine()) {
            val line = `in`.nextLine()
            if (line == "q") {
                println("Quit..")
                break
            }
            redisTemplate.convertAndSend(chatRoomName, line)
        }

        container.removeMessageListener(this)
    }

    override fun onMessage(message: Message, pattern: ByteArray?) {
        println("Message: $message")
    }

    @Bean
    fun redisContainer(redisConnectionFactory : RedisConnectionFactory): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory)
        return container
    }

}

