package com.yscorp.lgtm.common

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import reactor.core.publisher.Mono
import java.net.URI

@Component
@Order(-2)  // 이 핸들러가 글로벌 예외 처리기보다 먼저 실행되도록 설정
class GlobalErrorWebExceptionHandler(
    errorAttributes: CustomErrorAttributes,
    applicationContext: ApplicationContext,
    serverCodecConfigurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(errorAttributes, WebProperties.Resources(), applicationContext) {

    private val log = KotlinLogging.logger {}

    init {
        this.setMessageWriters(serverCodecConfigurer.writers)
        this.setMessageReaders(serverCodecConfigurer.readers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all()) { request ->
            handleGlobalException(request)
        }
    }

    private fun handleGlobalException(request: ServerRequest): Mono<ServerResponse> {
        val error = getError(request)

        log.error { "Handling unhandled exception: ${error.message}" }

        val errorResponse = ErrorResponse(
            type = URI.create("https://example.com/unhandled-error"),
            title = "Unhandled Error",
            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
            detail = error.message ?: "Unexpected error occurred",
            instance = URI.create(request.uri().toString()),
            code = "UNHANDLED_ERROR"
        )

        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(errorResponse)
    }
}

@Component
class CustomErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(
        request: ServerRequest,
        options: ErrorAttributeOptions
    ): Map<String, Any> {
        val errorAttributes = super.getErrorAttributes(request, options)
        errorAttributes["customField"] = "Additional information for unhandled exceptions"
        return errorAttributes
    }
}