package com.ys.springbootdistributedlock.application

import com.ys.springbootdistributedlock.domain.item.Item
import com.ys.springbootdistributedlock.domain.item.ItemRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.stream.IntStream


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class ItemLettuceServiceTest {

    @Autowired
    lateinit var itemLettuceService: ItemLettuceService

    @Autowired
    lateinit var itemRepository: ItemRepository

    @DisplayName("재고 감소 - lettuce lock")
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
                        itemLettuceService.decrease(itemId, quantity)
                    } catch (e: Exception) {
                        throw RuntimeException(e)
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

        assertThat(afterQuantity).isZero
    }

}