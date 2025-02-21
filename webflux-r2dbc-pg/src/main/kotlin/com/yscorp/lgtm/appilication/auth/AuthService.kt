package com.yscorp.lgtm.appilication.auth

import com.yscorp.lgtm.config.security.JwtTokenProvider
import com.yscorp.lgtm.config.security.UserJwtPrincipal
import com.yscorp.lgtm.domain.User
import com.yscorp.lgtm.domain.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
) {

    private val log = KotlinLogging.logger {  }

    @Transactional
    suspend fun join(nickname: String, username: String, password: String): User {
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("이미 존재하는 username 입니다.")
        }

        val user = User(
            nickname,
            username,
            password,
        )

        return userRepository.save(user)
    }

    suspend fun login(username: String, password: String): Pair<String, LocalDateTime> {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("존재하지 않는 username 입니다.")

        if (user.password != password) {
            throw IllegalArgumentException("비밀번호가 일치하지 않습니다.")
        }

        val createAccessToken = jwtTokenProvider.createAccessToken(user.id, user.username, user.role)

        log.info { "createAccessToken: $createAccessToken"}
        return createAccessToken
    }

    suspend fun me(principal: UserJwtPrincipal): User {
        return userRepository.findById(principal.userId)!!
    }

}