package com.ys.springbootnamedlockjpa.application

import com.ys.springbootnamedlockjpa.domain.item.ItemService
import com.ys.springbootnamedlockjpa.infra.NamedLockWithJdbcTemplate
import org.springframework.stereotype.Service

@Service
class ItemNamedLockService(
    private val itemService: ItemService,
    private val namedLockWithJdbcTemplate: NamedLockWithJdbcTemplate
) {

    fun decreaseStock(itemId: Long, quantity: Long) {
        val key = LOCK_PREFIX + itemId.toString()

        val result = namedLockWithJdbcTemplate.executeWithLock("key", 3000)
        { itemService.decrease(itemId, quantity) }
    }

    companion object {
        private const val LOCK_PREFIX = "LOCK:"
    }

}