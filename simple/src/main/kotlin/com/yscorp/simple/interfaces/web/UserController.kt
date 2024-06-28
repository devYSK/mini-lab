package com.yscorp.simple.interfaces.web

import com.yscorp.simple.common.CommonResponse
import com.yscorp.simple.domain.user.User
import com.yscorp.simple.domain.user.UserService
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping
    fun createUser(@RequestBody user: User): Mono<CommonResponse<User>> {
        return userService.createUser(user)
            .map { CommonResponse("success", "User created successfully", it) }
    }

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): Mono<CommonResponse<User>> {
        return userService.getUser(id)
            .map { CommonResponse("success", "User retrieved successfully", it) }
//            .onErrorResume {
//                Mono.just(CommonResponse("error", it.message ?: "Unknown error"))
//            }
    }

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody user: User): Mono<CommonResponse<User>> {
        return userService.updateUser(user)
            .map { CommonResponse("success", "User updated successfully", it) }
    }

    @GetMapping
    fun getUsers(): Mono<CommonResponse<List<User>>> {
        return userService.getUsers()
            .collectList() // Flux<User>를 List<User>로 변환
            .map { users -> CommonResponse("success", "Users retrieved successfully", users) }
    }

}
