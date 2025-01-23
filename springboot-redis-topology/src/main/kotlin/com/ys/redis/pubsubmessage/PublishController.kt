package com.ys.redis.pubsubmessage

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class PublishController(
    private val redisTemplate: RedisTemplate<String, String>
) {

    @PostMapping("/events/users/deregister")
    fun publishUserDeregisterEvent() {
        redisTemplate.convertAndSend("users:unregister", "500")
    }

}

