package com.yscorp.webflux.common

data class RestResponse<T>(
    val data: T,
    val message: String? = null,
    val code: String? = null
) {
}