package com.ys.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component



//  SpEL(Spring Expression Language)을 지원하지만 외부 설정 파일에서 값을 직접 참조하는 용도로 사용할 수 없다.
@Component
class CRUDRedisService(
    private val stringRedisTemplate: StringRedisTemplate,
    private val redisTemplate: RedisTemplate<String, Any>,
) {

    fun incrementLong(key: String) {
        stringRedisTemplate.opsForValue().increment(key)
    }

    fun decrementLong(key: String) {
        stringRedisTemplate.opsForValue().decrement(key)
    }

    fun getLong(key: String): Long? {
        val value = stringRedisTemplate.opsForValue()[key]
        return value?.toLong()
    }
    
    // List
    fun addToList(key: String, vararg values: String) {
        stringRedisTemplate.opsForList().rightPushAll(key, *values)
    }

    fun getList(key: String): List<String> {
        return stringRedisTemplate.opsForList().range(key, 0, -1) ?: mutableListOf()
    }

    fun removeFromList(key: String, value: String) {
        stringRedisTemplate.opsForList().remove(key, 1, value)
    }

    // User 타입
    fun saveUser(user: User) {
        redisTemplate.opsForValue()[user.id.toString()] = user
    }

    fun getUser(id: String): Any? {
        return redisTemplate.opsForValue()[id]
    }

    fun deleteUser(id: String) {
        redisTemplate.delete(id)
    }

    fun updateUser(id: String, newUser: User) {
        saveUser(newUser) // Redis에서는 set 명령어가 업데이트 역할을 함
    }
}