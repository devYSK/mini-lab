package com.yscorp.webflux.config.security

import com.yscorp.lgtm.common.ErrorResponse
import com.yscorp.lgtm.config.objectMapper
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import org.springframework.web.server.ServerWebExchange
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import java.net.URI

@Component
class CustomAccessDeniedHandler : ServerAccessDeniedHandler {

    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.FORBIDDEN

        val errorResponse = ErrorResponse(
            type = URI.create("https://example.com/forbidden"),
            title = "Access Denied",
            status = HttpStatus.FORBIDDEN.value(),
            detail = denied.message,
            instance = URI.create(exchange.request.uri.toString()),
            code = "ACCESS_DENIED_ERROR"
        )

        val dataBuffer = response.bufferFactory().wrap(objectMapper.writeValueAsBytes(errorResponse))
        return response.writeWith(Mono.just(dataBuffer))
    }
}
