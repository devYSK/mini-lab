package com.ys.redis.leaderboard

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service


@Service
class RankingService(
    var redisTemplate: StringRedisTemplate,
) {
    
    fun setUserScore(userId: String, score: Int): Boolean {
        val zSetOps = redisTemplate.opsForZSet()
        zSetOps.add(LEADERBOARD_KEY, userId, score.toDouble())
        return true
    }

    fun getUserRanking(userId: String): Long? {
        val zSetOps = redisTemplate.opsForZSet()
        return zSetOps.reverseRank(LEADERBOARD_KEY, userId)
    }

    fun getTopRank(limit: Int): List<String> {
        val zSetOps = redisTemplate.opsForZSet()
        val rangeSet = zSetOps.reverseRange(LEADERBOARD_KEY, 0, (limit - 1).toLong())
      
        return ArrayList(rangeSet)
    }

    companion object {
        private const val LEADERBOARD_KEY = "leaderBoard"
    }

}

