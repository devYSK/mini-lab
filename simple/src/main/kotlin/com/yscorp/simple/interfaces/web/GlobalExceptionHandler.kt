package com.yscorp.simple.interfaces.web

import com.yscorp.simple.common.ErrorDetails
import com.yscorp.simple.common.ErrorResponse
import com.yscorp.simple.logger
import jakarta.validation.ConstraintViolationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(Exception::class)
    fun handleAllExceptions(ex: Exception, exchange: ServerWebExchange): Mono<ResponseEntity<ErrorResponse>> {
        log.error(ex) { "An unexpected error occurred: ${ex::class.java.name}: ${ex.message ?: ""}" }

        val errorResponse = ErrorResponse.internalServerError(
            message = ex.localizedMessage ?: "Internal server error",
            path = exchange.request.uri.path
        )
        return Mono.just(ResponseEntity.status(500).body(errorResponse))
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException, exchange: ServerWebExchange): Mono<ResponseEntity<ErrorResponse>> {
        val errorResponse = ErrorResponse.badRequest(
            message = ex.message ?: "Bad request",
            path = exchange.request.uri.path
        )

        return Mono.just(ResponseEntity.status(400).body(errorResponse))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException, exchange: ServerWebExchange): Mono<ResponseEntity<ErrorResponse>> {
        val details = ex.constraintViolations.map { violation ->
            ErrorDetails(field = violation.propertyPath.toString(), message = violation.message)
        }
        val errorResponse = ErrorResponse.badRequest(
            message = ex.message ?: "Bad request",
            path = exchange.request.uri.path,
            details = details
        )
        return Mono.just(ResponseEntity.status(400).body(errorResponse))
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(ex: WebExchangeBindException, exchange: ServerWebExchange): Mono<ResponseEntity<ErrorResponse>> {
        val details = ex.fieldErrors.map { error ->
            ErrorDetails(field = error.field, message = error.defaultMessage ?: "Invalid value")
        }

        val errorResponse = ErrorResponse.badRequest(
            message = ex.message,
            path = exchange.request.uri.path,
            details = details
        )

        return Mono.just(ResponseEntity.status(400).body(errorResponse))
    }

    companion object {
        private val log = logger()
    }

}
