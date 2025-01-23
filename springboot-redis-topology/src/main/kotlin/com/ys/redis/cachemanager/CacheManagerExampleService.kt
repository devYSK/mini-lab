package com.ys.redis.cachemanager

import com.ys.redis.User
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CacheManagerExampleService {

    // String 타입 조회
    @Cacheable(cacheNames = ["stringCache"], key = "#key")
    fun getStringValue(key: String): String {
        return "someStringValue" // 데이터 소스에서 조회
    }

    // Long 타입 조회
    @Cacheable(cacheNames = ["longCache"], key = "#key")
    fun getLongValue(key: String): Long {
        return 12345L // 데이터 소스에서 조회
    }

    // List 타입 조회
    @Cacheable(cacheNames = ["listCache"], key = "#key")
    fun getListValue(key: String): List<String> {
        return listOf("element1", "element2") // 데이터 소스에서 조회
    }

    // User 객체 조회
    @Cacheable(cacheNames = ["userCache"], key = "#id")
    fun getUser(id: Long): User {
        // 데이터 소스에서 조회
        return User(id, "username", "password", LocalDateTime.now())
    }

    // User 객체 업데이트
    @CachePut(cacheNames = ["userCache"], key = "#user.id")
    fun updateUser(user: User): User {
        // 데이터 소스에서 업데이트
        return user
    }

    // 캐시에서 항목 삭제
    @CacheEvict(cacheNames = ["userCache"], key = "#id")
    fun deleteUser(id: Long) {
        // 데이터 소스에서 삭제
    }

    // 모든 캐시 항목 삭제
    @CacheEvict(cacheNames = ["stringCache", "longCache", "listCache", "userCache"], allEntries = true)
    fun clearCache() {
        // 캐시에서 모든 항목 삭제
    }

}