package com.ys.springbootdistributedlock.domain.item

import com.ys.springbootdistributedlock.config.logger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemService(
    private val itemRepository: ItemRepository,
) {

    @Transactional
    fun decrease(itemId: Long, quantity: Long) {
        val item = itemRepository.findById(itemId)
            .orElseThrow()

        item.decrease(quantity)
        logger().info("재고 감소. 현재 재고 : ${item.stockQuantity}")
    }

}