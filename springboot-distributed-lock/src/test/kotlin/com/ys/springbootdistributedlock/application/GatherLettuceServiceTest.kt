package com.ys.springbootdistributedlock.application

import com.ys.springbootdistributedlock.application.GatherLettuceService
import com.ys.springbootdistributedlock.domain.group.Gather
import com.ys.springbootdistributedlock.domain.group.GatherRepository
import com.ys.springbootdistributedlock.domain.user.User
import com.ys.springbootdistributedlock.domain.user.UserRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.stream.IntStream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class GatherLettuceServiceTest {

    @Autowired
    lateinit var gatherLettuceService: GatherLettuceService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var gatherRepository: GatherRepository


    @DisplayName("그룹 가입- lettuce 스핀 lock")
    @Test
    fun join() {
        // given
        val threadCount = 100
        val executorService = Executors.newFixedThreadPool(threadCount)
        val countDownLatch = CountDownLatch(threadCount)

        val limit = 5
        val user = User.create("그룹장")
        val gather = Gather(limit, user)

        gatherRepository.save(gather)
        val createUsers = createUser(100)

        // when
        IntStream.range(0, threadCount)
            .forEach {

                executorService.submit {
                    try {
                        gatherLettuceService.join(gather.id!!, createUsers[it].id!!)
                    } catch (ex: InterruptedException) {
                        throw RuntimeException(ex)
                    } finally {
                        countDownLatch.countDown()
                    }
                }

            }

        countDownLatch.await()
        executorService.shutdown()

        // then
        val findGroup = gatherRepository.findById(gather.id!!).get()

        println("### findGroup.count=${findGroup.currentMemberCount}")

        Assertions.assertThat(findGroup.currentMemberCount).isEqualTo(limit)
    }

    @Transactional
    fun createUser(count: Int): List<User> {

        return IntStream.range(0, count).mapToObj { it ->
            userRepository.save(User.create(it.toString()))
        }.toList()
    }

    @AfterEach
    fun after() {
        userRepository.deleteAll()
        gatherRepository.deleteAll()
    }
}