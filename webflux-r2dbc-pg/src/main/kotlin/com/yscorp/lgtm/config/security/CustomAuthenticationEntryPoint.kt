package com.yscorp.lgtm.config.security

import com.yscorp.lgtm.common.ErrorResponse
import com.yscorp.lgtm.config.objectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URI


@Component
class CustomAuthenticationEntryPoint : ServerAuthenticationFailureHandler {

    private val log = KotlinLogging.logger { }

//    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> {
//
//        log.error { "AuthenticationEntryPoint error occurred. ${ex.message}" }
//
//        val errorResponse = ErrorResponse(
//            type = URI.create("https://example.com/server-web-input-error"),
//            title = "Server Web Input Error",
//            status = HttpStatus.BAD_REQUEST.value(),
//            detail = ex?.message,
//            instance = URI.create(exchange.request.uri.toString()),
//            code = "SERVER_WEB_INPUT_ERROR"
//        )
//
//        val serverHttpResponse = exchange.response
//        serverHttpResponse.headers.contentType = MediaType.APPLICATION_JSON
//        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED)
//
//        val dataBuffer = serverHttpResponse.bufferFactory()
//            .wrap(objectMapper.writeValueAsBytes(errorResponse))
//        return serverHttpResponse.writeWith(Mono.just(dataBuffer))
//
//    }

    override fun onAuthenticationFailure(
        webFilterExchange: WebFilterExchange,
        ex: AuthenticationException,
    ): Mono<Void> {
        val exchange = webFilterExchange.exchange

        log.error { "AuthenticationEntryPoint error occurred. ${ex.message}" }

        val errorResponse = ErrorResponse(
            type = URI.create("https://example.com/server-web-input-error"),
            title = "Server Web Input Error",
            status = HttpStatus.UNAUTHORIZED.value(),
            detail = ex?.message,
            instance = URI.create(exchange.request.uri.toString()),
            code = "SERVER_WEB_INPUT_ERROR"
        )

        val serverHttpResponse = exchange.response
        serverHttpResponse.headers.contentType = MediaType.APPLICATION_JSON
        serverHttpResponse.setStatusCode(HttpStatus.UNAUTHORIZED)

        val dataBuffer = serverHttpResponse.bufferFactory()
            .wrap(objectMapper.writeValueAsBytes(errorResponse))
        return serverHttpResponse.writeWith(Mono.just(dataBuffer))

    }

}