package com.ys.springbootnamedlockjpa.application

import com.ys.springbootnamedlockjpa.domain.item.Item
import com.ys.springbootnamedlockjpa.domain.item.ItemRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.stream.IntStream


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class ItemNamedLockServiceTest {

    @Autowired
    lateinit var itemNamedLockService: ItemNamedLockService

    @Autowired
    lateinit var itemRepository: ItemRepository

    @DisplayName("재고 감소 - jdbc named lock")
    @Test
    fun stock_decrease() {
        // given
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(threadCount)
        val countDownLatch = CountDownLatch(threadCount)

        val item = Item(50L, "트랙패드")
        itemRepository.save(item)

        val itemId = item.id!!
        val quantity = 1L

        val tasks = IntStream.range(0, threadCount)
            .mapToObj {
                Runnable {
                    try {
                        itemNamedLockService.decreaseStock(itemId, quantity)
                    } catch (ex: InterruptedException) {
                        throw RuntimeException(ex)
                    } finally {
                        countDownLatch.countDown()
                    }
                }
            }.toList()

        // when
        tasks.forEach { executorService.submit(it) }
        countDownLatch.await()
        executorService.shutdown()

        // then
        val afterQuantity: Long = itemRepository.findById(itemId).get().stockQuantity

        println("### afterQuantity=$afterQuantity")

        Assertions.assertThat(afterQuantity).isZero
    }


    @AfterEach
    fun after() {
        itemRepository.deleteAll()
    }
}