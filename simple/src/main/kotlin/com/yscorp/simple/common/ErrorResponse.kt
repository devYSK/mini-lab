package com.yscorp.simple.common

import java.time.LocalDateTime

data class ErrorDetails(
    val field: String,
    val message: String
)

data class ErrorResponse(
    val timestamp: LocalDateTime,
    val status: Int,
    val message: String,
    val path: String,
    val details: List<ErrorDetails> = emptyList()
) {
    companion object {
        fun of(status: Int, message: String, path: String, details: List<ErrorDetails> = emptyList()): ErrorResponse {
            return ErrorResponse(
                timestamp = LocalDateTime.now(),
                status = status,
                message = message,
                path = path,
                details = details
            )
        }

        fun badRequest(message: String, path: String, details: List<ErrorDetails> = emptyList()): ErrorResponse {
            return of(400, message, path, details)
        }

        fun unauthorized(message: String, path: String): ErrorResponse {
            return of(401, message, path)
        }

        fun forbidden(message: String, path: String): ErrorResponse {
            return of(403, message, path)
        }

        fun notFound(message: String, path: String): ErrorResponse {
            return of(404, message, path)
        }

        fun internalServerError(message: String, path: String): ErrorResponse {
            return of(500, message, path)
        }
    }
}
