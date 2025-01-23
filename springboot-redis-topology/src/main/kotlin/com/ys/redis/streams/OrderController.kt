package com.ys.redis.streams

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class OrderController(
    var redisTemplate: StringRedisTemplate
) {

    @GetMapping("/order")
    fun order(
        @RequestParam userId: String,
        @RequestParam productId: String,
        @RequestParam price: String,
    ): String {
        val fieldMap = HashMap<String, String>()
        fieldMap["userId"] = userId
        fieldMap["productId"] = productId
        fieldMap["price"] = price

        redisTemplate.opsForStream<Any, Any>().add("order-events", fieldMap)
        println("Order created.")

        return "ok"
    }
    
}

