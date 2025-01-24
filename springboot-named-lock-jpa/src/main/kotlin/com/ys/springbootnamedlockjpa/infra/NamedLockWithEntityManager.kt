package com.ys.springbootnamedlockjpa.infra

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Query
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
class NamedLockWithEntityManager(
    @PersistenceContext
    private val entityManager: EntityManager
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    fun getLock(userLockName: String, timeoutSeconds: Int): Boolean {
        val query: Query = entityManager.createNativeQuery("SELECT GET_LOCK(:userLockName, :timeoutSeconds)")
            .setParameter("userLockName", userLockName)
            .setParameter("timeoutSeconds", timeoutSeconds)

        return convertResult(query.singleResult as Int?)
    }

    @Transactional
    fun releaseLock(userLockName: String): Boolean {
        // Release the named lock
        val query: Query = entityManager.createNativeQuery("DO RELEASE_LOCK(:userLockName)")
            .setParameter("userLockName", userLockName)

        return convertResult(query.singleResult as Int?)
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
}