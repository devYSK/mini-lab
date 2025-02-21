package com.yscorp.lgtm.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher

@Configuration
@EnableReactiveMethodSecurity
@EnableWebFluxSecurity
class WebFluxSecurityConfig(
    private val jwtProvider: JwtTokenProvider,
    private val authenticationEntryPoint: ServerAuthenticationFailureHandler,
) {

    val whiteList = listOf("/api/v1/auth/login", "/api/v1/auth/signup")

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .httpBasic { it.disable() }
            .authorizeExchange { exchanges ->
                exchanges
                    .pathMatchers(*whiteList.toTypedArray()).permitAll()  // 인증이 필요 없는 경로
                    .anyExchange().authenticated()  // 나머지 모든 요청은 인증 필요
            }
            // '/api/v1/auth/**' 경로는 필터를 통과하지 않도록 설정
            .addFilterBefore(JwtAuthenticationWebFilter(jwtProvider, authenticationEntryPoint).apply {
                setRequiresAuthenticationMatcher { exchange ->
                    val uri = exchange.request.uri
                    log.info("Request URI: ${uri.path}")

                    if (whiteList.contains(uri.path)) {
                        log.info { "Skip JWT authentication" }
                        ServerWebExchangeMatcher.MatchResult.notMatch()
                    } else {
                        log.info { "JWT authentication" }
                        ServerWebExchangeMatcher.MatchResult.match()
                    }
                }
            }, SecurityWebFiltersOrder.AUTHENTICATION)
            .exceptionHandling {
                it //.authenticationEntryPoint(authenticationEntryPoint)
                    .accessDeniedHandler(CustomAccessDeniedHandler())
            }
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())  // 시큐리티 컨텍스트 저장하지 않음 (JWT 무상태)
            .build()
    }

    // 예시용 유저 설정, 실제 서비스에서는 DB나 외부 인증 시스템을 사용할 수 있습니다.
//    @Bean
//    fun userDetailsService(): MapReactiveUserDetailsService {
//        val user: UserDetails = User.withUsername("user")
//            .password(passwordEncoder().encode("password"))
//            .roles("USER")
//            .build()
//        return MapReactiveUserDetailsService(user)
//    }
}