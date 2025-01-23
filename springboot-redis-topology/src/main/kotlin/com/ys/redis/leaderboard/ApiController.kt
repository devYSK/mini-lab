package com.ys.redis.leaderboard

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class ApiController(
    private val rankingService: RankingService
) {
    
    @GetMapping("/setScore")
    fun setScore(
        @RequestParam userId: String,
        @RequestParam score: Int,
    ): Boolean {
        return rankingService.setUserScore(userId, score)
    }

    @GetMapping("/getRank")
    fun getUserRank(
        @RequestParam userId: String,
    ): Long {
        return rankingService.getUserRanking(userId)
    }

    @get:GetMapping("/getTopRanks")
    val topRanks: List<String>
        get() = rankingService.getTopRank(3)
}

