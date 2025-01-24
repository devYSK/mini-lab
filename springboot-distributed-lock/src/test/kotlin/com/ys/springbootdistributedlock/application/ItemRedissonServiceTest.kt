package com.ys.springbootdistributedlock.application

import com.ys.springbootdistributedlock.domain.item.Item
import com.ys.springbootdistributedlock.domain.item.ItemRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.stream.Collectors
import java.util.stream.IntStream


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class ItemRedissonServiceTest {

    @Autowired
    lateinit var itemRedissonService: ItemRedissonService

    @Autowired
    lateinit var itemRepository: ItemRepository

    @DisplayName("재고 감소 - redisson lock")
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
                        itemRedissonService.decreaseStock(itemId, quantity)
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

        assertThat(afterQuantity).isZero
    }

    @Test
    fun 비동기_테스트() {
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(threadCount)

        val item = Item(50L, "트랙패드")
        itemRepository.save(item)

        val itemId = item.id!!
        val quantity = 1L
        // IntStream을 사용하여 CompletableFuture 리스트를 생성합니다.
        val futures: List<CompletableFuture<Void>> = IntStream.range(0, threadCount)
            .mapToObj {
                CompletableFuture.runAsync({
                    try {
                        itemRedissonService.decreaseStock(itemId, quantity)
                    } catch (ex: Exception) {
                        println(ex)
                    }
                }, executorService)
            }
            .collect(Collectors.toList())

        // 모든 CompletableFuture가 완료될 때까지 기다립니다.
        CompletableFuture.allOf(*futures.toTypedArray()).join()

        // ExecutorService를 종료합니다.
        executorService.shutdown()

        // then
        val afterQuantity: Long = itemRepository.findById(itemId).get().stockQuantity

        println("### afterQuantity=$afterQuantity")

        assertThat(afterQuantity).isZero
    }

    @AfterEach
    fun after() {
        itemRepository.deleteAll()
    }
}

