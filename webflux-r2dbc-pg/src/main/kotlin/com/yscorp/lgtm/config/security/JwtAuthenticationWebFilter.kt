package com.yscorp.lgtm.config.security

import com.yscorp.webflux.config.security.JwtTokenProvider
import com.yscorp.webflux.config.security.UserJwtPrincipal
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


val log = KotlinLogging.logger { }

class JwtAuthenticationWebFilter(
    private val jwtProvider: JwtTokenProvider,
    private val authenticationEntryPoint: ServerAuthenticationFailureHandler,
) : AuthenticationWebFilter(ReactiveAuthenticationManager { Mono.just(it) }) {

    init {
        this.setServerAuthenticationConverter(JwtServerAuthenticationConverter(jwtProvider))
        this.setAuthenticationFailureHandler(authenticationEntryPoint)
    }

}

class JwtServerAuthenticationConverter(
    private val jwtProvider: JwtTokenProvider,
) : ServerAuthenticationConverter {

    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        val token: String? = exchange.request.headers[HttpHeaders.AUTHORIZATION]?.firstOrNull()
        if (token.isNullOrEmpty()) {
            return Mono.error(UnAuthenticationException("Authorization header is missing"))
        }

        if (!token.startsWith("Bearer ")) {
            return Mono.error(UnAuthenticationException("Authorization header is invalid"))
        }

        return try {
            val accessToken = token.substring(7)
            jwtProvider.validateJwtToken(accessToken)
            val tokenInfo = jwtProvider.parse(accessToken)

            val principal = UserJwtPrincipal(tokenInfo.userId, tokenInfo.username, tokenInfo.role, tokenInfo.token)

            val authentication = UsernamePasswordAuthenticationToken(
                principal, "", principal.authorities
            )

            Mono.just(authentication)
        } catch (e: Exception) {
            Mono.error(UnAuthenticationException("Token parsing failed", e))
        }
    }
}