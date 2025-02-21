package com.yscorp.lgtm.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebInputException
import java.net.URI

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = KotlinLogging.logger {  }

    /**
     * @Valid 또는 @Validated 검증 실패 시 발생하는 예외를 처리합니다.
     * 오류 필드와 메시지를 properties에 포함하여 반환합니다.
     */
    @ExceptionHandler(WebExchangeBindException::class)
    suspend fun handleWebExchangeBindException(ex: WebExchangeBindException, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }
        val errorResponse = ErrorResponse(
            type = URI.create("https://example.com/validation-error"),
            title = "Validation Error",
            status = HttpStatus.BAD_REQUEST.value(),
            detail = "입력값이 올바르지 않습니다.",
            instance = URI.create(exchange.request.uri.toString()),
            properties = mapOf("invalidFields" to fieldErrors),
            code = "VALIDATION_ERROR"
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    suspend fun handleException(ex: Exception, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Unexpected error occurred. ${ex.javaClass.name}" }

        val errorResponse = ErrorResponse(
            type = URI.create("https://example.com/internal-server-error"),
            title = "Internal Server Error",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            detail = "예기치 않은 오류가 발생했습니다.",
            instance = URI.create(exchange.request.uri.toString()),
            code = "INTERNAL_SERVER_ERROR"
        )

        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(ServerWebInputException::class)
    suspend fun handleServerWebInputException(ex: ServerWebInputException, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
        log.error(ex) { "ServerWebInputException error occurred. ${ex.message}" }

        // DecodingException 추출
        val cause = ex.cause
        if (cause is DecodingException) {
            val decodingException = cause as DecodingException

            // 원인 추출 및 상세 메시지 출력
            val rootCause = decodingException.cause
            val message = decodingException.message

            // 원인 및 메세지 로그 출력
            log.error("DecodingException: ${message}")
            log.error("Root Cause: ${rootCause?.message}")
        }


        log.info { """
            methodParameter : ${ex.methodParameter},
            body : ${ex.body},
            message : ${ex.message},
        """.trimIndent() }


        val errorResponse = ErrorResponse(
            type = URI.create("https://example.com/server-web-input-error"),
            title = "Server Web Input Error",
            status = HttpStatus.BAD_REQUEST.value(),
            detail = "입력 오류가 발생했습니다.",
            instance = URI.create(exchange.request.uri.toString()),
            code = "SERVER_WEB_INPUT_ERROR"
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(DecodingException::class)
    suspend fun handleDecodingException(ex: DecodingException, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
        log.error(ex) { "Decoding error occurred. ${ex.message}" }

        val errorResponse = ErrorResponse(
            type = URI.create("https://example.com/decoding-error"),
            title = "Decoding Error",
            status = HttpStatus.BAD_REQUEST.value(),
            detail = "디코딩 중 오류가 발생했습니다.",
            instance = URI.create(exchange.request.uri.toString()),
            code = "DECODING_ERROR"
        )

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

//    /**
//     * javax.validation 제약 조건이 실패할 때 발생하는 예외를 처리합니다.
//     * 각 제약 조건 위반 정보를 properties에 포함하여 반환합니다.
//     */
//    @ExceptionHandler(ConstraintViolationException::class)
//    suspend fun handleConstraintViolationException(ex: ConstraintViolationException, exchange: ServerWebExchange): ResponseEntity<ErrorResponse> {
//        val violations = ex.constraintViolations.associate { it.propertyPath.toString() to it.message }
//        val errorResponse = ErrorResponse(
//            type = URI.create("https://example.com/constraint-violation"),
//            title = "Constraint Violation",
//            status = HttpStatus.BAD_REQUEST.value(),
//            detail = "제약 조건을 위반했습니다.",
//            instance = URI.create(exchange.request.uri.toString()),
//            properties = mapOf("violations" to violations),
//            code = "CONSTRAINT_VIOLATION"
//        )
//        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
//    }
}