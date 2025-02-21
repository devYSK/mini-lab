package com.yscorp.lgtm.domain

import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<User, Long>, UserRepositoryCustom {

    fun findByName(name: String): Flow<User>
    fun findByNameOrderByIdDesc(name: String): Flow<User>

    @Modifying
    @Query("DELETE FROM users WHERE name = :name")
    suspend fun deleteByName(name: String): Int

    suspend fun existsByUsername(username: String): Boolean

    suspend fun findByUsername(username: String): User?

}
