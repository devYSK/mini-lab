package com.ys.springbootdistributedlock.application

import com.ys.springbootdistributedlock.config.logger
import com.ys.springbootdistributedlock.domain.group.GatherService
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class GatherRedissonService(
    private val gatherService: GatherService,
    private val redissonClient: RedissonClient,
) {

    fun join(groupId: Long, userId: Long) {
        val key = LOCK_PREFIX + groupId.toString()
        val lock: RLock = redissonClient.getLock(key)

        try {
            // 락 획득. (락 획득을 대기할 타임아웃, 락이 만료되는 시간)
            val isAvailable = lock.tryLock(5, 3, TimeUnit.SECONDS)

            if (!isAvailable) {
                log.info("redisson getLock fail.")
                return
            }

            gatherService.join(groupId, userId)

        } finally {
            // 락 해제
            lock.unlock()
        }
    }

    companion object {
        private val log: Logger = logger()
        private const val LOCK_PREFIX = "LOCK:"
    }

}