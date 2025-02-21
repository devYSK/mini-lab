package com.yscorp.lgtm.interfaces.api

import com.yscorp.lgtm.common.RestResponse
import com.yscorp.lgtm.domain.User
import com.yscorp.lgtm.domain.UserService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {

    @GetMapping
    fun findAll(): Flow<User> {
        return userService.findAll()
    }

    @GetMapping("/to")
    suspend fun findAllTo(): RestResponse<List<User>> {
        return RestResponse(
            data = userService.findAll().toList()
        )
    }

    @GetMapping("/{id}")
    suspend fun findById(@PathVariable id: Long): ResponseEntity<User?> {
        val user = userService.findById(id)
        return if (user != null) {
            ResponseEntity.ok(user)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @DeleteMapping("/{id}")
    suspend fun deleteById(@PathVariable id: Long): ResponseEntity<Void> {
        userService.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/name/{name}")
    suspend fun deleteByName(@PathVariable name: String): ResponseEntity<Void> {
        userService.deleteByName(name)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{id}")
    suspend fun update(
        @PathVariable id: Long,
        @RequestParam name: String,
        @RequestParam email: String,
    ): ResponseEntity<User?> {
        val updatedUser = userService.update(id, name, email)

        return if (updatedUser != null) {
            ResponseEntity.ok(updatedUser)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
