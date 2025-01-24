package com.ys.springbootnamedlockjpa.infra

import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.function.Supplier

@Repository
class NamedLockWithJdbcTemplate(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun <T> executeWithLock(
        userLockName: String,
        timeoutSeconds: Int,
        supplier: Supplier<T>
    ): T {
        return try {
            getLock(userLockName, timeoutSeconds)
            supplier.get()
        } finally {
            releaseLock(userLockName)
        }
    }

    @Transactional
    fun getLock(userLockName: String, timeoutSeconds: Int): Boolean {
        val sql = "SELECT GET_LOCK(:userLockName, :timeoutSeconds)"
        val params = mapOf("userLockName" to userLockName, "timeoutSeconds" to timeoutSeconds)

        return convertResult(jdbcTemplate.queryForObject(sql, params, Int::class.java))
    }

    @Transactional
    fun releaseLock(userLockName: String): Boolean {
        // Release the named lock
        val sql = "SELECT RELEASE_LOCK(:userLockName)"
        val params = mapOf("userLockName" to userLockName)

        return convertResult(jdbcTemplate.queryForObject(sql, params, Int::class.java))
    }

    private fun convertResult(result: Int?): Boolean {
        return when (result) {
            1 -> {
                log.info("lock 획득")
                true
            }
            0, null -> {
                log.info("lock 획득 실패")
                false
            }
            else -> {
                log.error("예상치 못한 결과: $result")
                false
            }
        }
    }

    companion object {
        private const val GET_LOCK = "SELECT GET_LOCK(:userLockName, :timeoutSeconds)"
        private const val RELEASE_LOCK = "SELECT RELEASE_LOCK(:userLockName)"
        private const val EXCEPTION_MESSAGE = "LOCK 을 수행하는 중에 오류가 발생하였습니다."
    }

}
