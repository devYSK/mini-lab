package com.ys.springbootnamedlockjpa.infra

import org.hibernate.cache.spi.support.AbstractReadWriteAccess.Item
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface NamedLockWithJpaRepository : JpaRepository<Item, Long> {

    @Query(value = "select GET_LOCK(:key, :timeoutSeconds)", nativeQuery = true)
    fun getLock(key: String, timeoutSeconds: Int): Long?

    @Query(value = "select RELEASE_LOCK(:key)", nativeQuery = true)
    fun releaseLock(key: String) : Long?
}