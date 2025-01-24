package com.ys.springbootnamedlockjpa.domain.item

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ItemService(
    private val itemRepository: ItemRepository,
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    fun decrease(itemId: Long, quantity: Long) {
        val item = itemRepository.findById(itemId)
            .orElseThrow()

        item.decrease(quantity)
        log.info("재고 감소. 현재 재고 : ${item.stockQuantity}")
    }

}