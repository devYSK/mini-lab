package com.ys.redis.leaderboard

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.time.Instant
import java.util.*


@SpringBootTest
class RankingServiceTest {

    @Autowired
    private val rankingService: RankingService? = null

    @Test
    fun getRanks() {
        rankingService!!.getTopRank(1)

        // 1) Get user_100's rank
        var before = Instant.now()
        val userRank = rankingService!!.getUserRanking("user_100")
        var elapsed = Duration.between(before, Instant.now())
        println(String.format("Rank(%d) - Took %d ms", userRank, elapsed.nano / 1000000))

        // 2) Get top 10 user list
        before = Instant.now()
        val topRankers = rankingService!!.getTopRank(10)
        elapsed = Duration.between(before, Instant.now())
        println(String.format("Range - Took %d ms", elapsed.nano / 1000000))
    }

    @Test
    fun insertScore() {
        for (i in 0..999999) {
            val score = (Math.random() * 1000000).toInt() // 0 ~ 999999
            val userId = "user_$i"
            rankingService!!.setUserScore(userId, score)
        }
    }

    @Test
    fun inMemorySortPerformance() {
        val list = ArrayList<Int>()
        for (i in 0..999999) {
            val score = (Math.random() * 1000000).toInt() // 0 ~ 999999
            list.add(score)
        }
        val before = Instant.now()
        Collections.sort(list) // nlogn
        val elapsed = Duration.between(before, Instant.now())
        println((elapsed.nano / 1000000).toString() + " ms")
    }
}