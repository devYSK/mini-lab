package com.ys.springbootdistributedlock.application

import com.ys.springbootdistributedlock.domain.item.ItemService
import com.ys.springbootdistributedlock.infra.RedisRepository
import org.springframework.stereotype.Service

@Service
class ItemLettuceService(
    private val redisRepository: RedisRepository,
    private val itemService: ItemService,

    ) {

    fun decrease(productId: Long, quantity: Long) {
        val key = LOCK_PREFIX + productId.toString()

        while (!redisRepository.lock(key, 3000)) {
            Thread.sleep(100)
        }

        try {
            itemService.decrease(productId, quantity)
        } finally {
            redisRepository.unlock(key)
        }
    }

    companion object {
        private const val LOCK_PREFIX = "LOCK:"
    }

}