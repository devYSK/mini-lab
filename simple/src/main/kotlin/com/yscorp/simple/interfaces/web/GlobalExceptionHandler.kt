package com.yscorp.simple.interfaces.web

import com.yscorp.simple.common.CommonResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): Mono<ResponseEntity<String>> {
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. ${ex::class.java.name}: ${ex.message ?: ""}"))
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handlerWebClientException(ex: WebExchangeBindException): Mono<ResponseEntity<CommonResponse<Any>>> {
        log.error(ex.message, ex)
        moaLogger.error(ex.message, ex)

        return Mono.just(ResponseEntity.badRequest().body(CommonResponse<Any>("400", ex.message,)))
    }

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
        private val moaLogger = LoggerFactory.getLogger("MoALogger")
    }
}
