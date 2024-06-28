package com.yscorp.simple.domain.user

import com.yscorp.simple.logger
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    private val log = logger()

    fun createUser(user: User): Mono<User> {
        log.info { "createUser: $user"}
        return userRepository.save(user)
    }

    fun getUser(id: Long): Mono<User> {
        return userRepository.findById(id).switchIfEmpty { Mono.error(NoSuchElementException("User not found"))}
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
