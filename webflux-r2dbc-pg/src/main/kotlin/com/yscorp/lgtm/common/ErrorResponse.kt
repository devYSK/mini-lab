package com.yscorp.lgtm.common

import java.net.URI
/**
 * Represents detailed error information in a standardized format.
 */
data class ErrorResponse(
    val type: URI = URI.create("about:blank"), // 문제의 유형을 설명하는 URI
    val title: String? = null,                // 문제에 대한 짧은 설명
    val status: Int,                          // HTTP 상태 코드
    val detail: String? = null,               // 문제에 대한 상세 설명
    val instance: URI? = null,                // 문제가 발생한 구체적인 URI 경로
    val properties: Map<String, Any>? = null, // 추가적인 오류 정보를 담을 수 있는 필드
    val code: String? = null                  // 추가된 필드: 에러 코드를 표현
)