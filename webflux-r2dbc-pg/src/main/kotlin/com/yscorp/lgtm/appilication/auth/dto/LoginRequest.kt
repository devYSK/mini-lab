package com.yscorp.webflux.appilication.auth.dto

data class LoginRequest(
    val username: String,
    val password: String,
) {
}