package com.ys.springbootdistributedlock.application

import com.ys.springbootdistributedlock.domain.group.GatherService
import com.ys.springbootdistributedlock.infra.RedisRepository
import org.springframework.stereotype.Service

@Service
class GatherLettuceService(
    private val redisRepository: RedisRepository,
    private val gatherService: GatherService,
    ) {

    fun join(groupId: Long, userId: Long) {
        val key = LOCK_PREFIX + groupId.toString()

        while (!redisRepository.lock(key, 3000)) {
            Thread.sleep(100)
        }

        try {
            gatherService.join(groupId, userId)
        } finally {
            redisRepository.unlock(key)
        }
    }

    companion object {
        private const val LOCK_PREFIX = "LOCK:"
    }

}