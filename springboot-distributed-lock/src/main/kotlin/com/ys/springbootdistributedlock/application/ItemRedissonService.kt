package com.ys.springbootdistributedlock.application

import com.ys.springbootdistributedlock.config.logger
import com.ys.springbootdistributedlock.domain.item.ItemService
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.Logger
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ItemRedissonService(
    private val itemService: ItemService,
    private val redissonClient: RedissonClient,
) {

    fun decreaseStock(productId: Long, quantity: Long) {
        val key = LOCK_PREFIX + productId.toString()
        val lock: RLock = redissonClient.getLock(key)

        try {
            // 락 획득. (락 획득을 대기할 타임아웃, 락이 만료되는 시간)
            val isAvailable = lock.tryLock(5, 3, TimeUnit.SECONDS)

            if (!isAvailable) {
                log.info("redisson getLock fail. ")
                return
            }

            // 재고 감소
            itemService.decrease(productId, quantity)

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