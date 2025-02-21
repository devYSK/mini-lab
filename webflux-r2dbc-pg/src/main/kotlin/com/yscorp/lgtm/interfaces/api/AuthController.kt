package com.yscorp.lgtm.interfaces.api

import com.yscorp.lgtm.appilication.auth.AuthService
import com.yscorp.lgtm.appilication.auth.dto.LoginRequest
import com.yscorp.lgtm.appilication.auth.dto.SignupRequest
import com.yscorp.lgtm.config.security.UserJwtPrincipal
import com.yscorp.lgtm.domain.User
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService,
) {
    private val log = KotlinLogging.logger {  }

    @PostMapping("/signup")
    suspend fun signup(
        @RequestBody request: SignupRequest
    ): User {
        return authService.join(request.nickname, request.username, request.password)
    }

    @PostMapping("/login")
    suspend fun login(
        @RequestBody request: LoginRequest
    ): Pair<String, LocalDateTime> {
        log.info { "login request: $request"}
        return authService.login(request.username, request.password)
    }

    @GetMapping("/me")
    suspend fun me(
        @AuthenticationPrincipal principal: UserJwtPrincipal
    ): User {
        log.info { "principal: $principal"}

        return authService.me(principal)
    }

}