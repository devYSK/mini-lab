package com.yscorp.lgtm.domain

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    fun findAll(): Flow<User> {
        return userRepository.findAll()
    }

    suspend fun findById(id: Long): User? {

        return userRepository.findById(id)
    }

    suspend fun deleteById(id: Long) {
        userRepository.deleteById(id)
    }

    suspend fun deleteByName(name: String) {
        userRepository.deleteByName(name)
    }

    suspend fun update(id: Long, name: String, email: String): User? {
        val user = userRepository.findById(id)

        return if (user != null) {
            user.name = name
            user.username = email
            userRepository.save(user)
        } else {
            null
        }
    }
}
