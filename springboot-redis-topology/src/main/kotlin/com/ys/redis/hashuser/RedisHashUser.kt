package com.ys.redis.hashuser

import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.time.LocalDateTime

/**
 * 단건 조회에 해당하는 Redis 쿼리는 HGETALL 명령어를 사용시 HGETALL redishash-user:1 로 조회.
 *
 * 전체 조회 SCAN 0 MATCH redishash-user:*
 * 결과
 * 1) "id"
 * 2) "1"
 * 3) "name"
 * 4) "John Doe"
 * 5) "email"
 * 6) "john.doe@example.com"
 * 7) "createdAt"
 * 8) "2023-03-17T12:34:56"
 * 9) "updatedAt"
 * 10) "2023-03-17T12:34:56"
 * @Indexed 어노테이션은 해당 필드를 색인(Index)하겠다는 것을 나타냅니다, 즉 email 필드에 대해 검색을 수행할 때 성능이 향상
 *
 */

@RedisHash(value = "redishash-user", timeToLive = 30L)
class RedisHashUser {
    @jakarta.persistence.Id
    private val id: Long? = null
    private val name: String? = null

    @Indexed
    private val email: String? = null
    private val createdAt: LocalDateTime? = null
}

