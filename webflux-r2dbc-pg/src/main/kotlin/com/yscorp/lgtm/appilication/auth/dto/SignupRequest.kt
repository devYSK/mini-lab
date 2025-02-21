package com.yscorp.lgtm.appilication.auth.dto

data class SignupRequest(
    val nickname: String,
    val username: String,
    val password: String,
) {
}