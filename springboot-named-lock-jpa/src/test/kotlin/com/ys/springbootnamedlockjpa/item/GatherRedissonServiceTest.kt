package com.ys.springbootdistributedlock.application.item

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class GatherRedissonServiceTest {
//
//    @Autowired
//    lateinit var gatherRedissonService: GatherRedissonService
//
//    @Autowired
//    lateinit var userRepository: UserRepository
//
//    @Autowired
//    lateinit var gatherRepository: GatherRepository
//
//
//    @DisplayName("그룹 가입- redisson lock")
//    @Test
//    fun join() {
//        // given
//        val threadCount = 100
//        val executorService = Executors.newFixedThreadPool(threadCount)
//        val countDownLatch = CountDownLatch(threadCount)
//
//        val limit = 5
//        val user = User.create("그룹장")
//        val gather = Gather(limit, user)
//
//        gatherRepository.save(gather)
//        val createUsers = createUser(100)
//
//        val tasks = IntStream.range(0, threadCount)
//            .mapToObj { it ->
//                Runnable {
//                    try {
//                        gatherRedissonService.join(gather.id!!, createUsers[it].id!!)
//                    } catch (ex: InterruptedException) {
//                        throw RuntimeException(ex)
//                    } finally {
//                        countDownLatch.countDown()
//                    }
//                }
//            }.toList()
//
//        // when
//        tasks.forEach { executorService.submit(it) }
//        countDownLatch.await()
//        executorService.shutdown()
//
//        // then
//        val findGroup = gatherRepository.findById(gather.id!!).get()
//
//        println("### findGroup.count=${findGroup.currentMemberCount}")
//
//        Assertions.assertThat(findGroup.currentMemberCount).isEqualTo(limit)
//    }
//
//    @Transactional
//    fun createUser(count: Int): List<User> {
//
//        return IntStream.range(0, count).mapToObj { it ->
//            userRepository.save(User.create(it.toString()))
//        }.toList()
//    }
//
//    @AfterEach
//    fun after() {
//        userRepository.deleteAll()
//        gatherRepository.deleteAll()
//    }

}
