package com.yscorp.simple.domain.user

import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    fun createUser(user: User): Mono<User> {
        return userRepository.save(user)
    }

    fun getUser(id: Long): Mono<User> {
        return userRepository.findById(id).switchIfEmpty(Mono.error(Exception("User not found")))
    }

    fun updateUser(user: User): Mono<User> {
        return userRepository.save(user)
    }

    fun deleteUser(id: Long) {
        userRepository.deleteById(id).subscribe()
    }

    fun getUsers(): Flux<User> {
        return userRepository.findAll()
    }

}