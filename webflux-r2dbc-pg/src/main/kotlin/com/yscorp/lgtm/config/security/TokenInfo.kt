package com.yscorp.lgtm.config.security

import com.yscorp.lgtm.domain.UserRole

data class TokenInfo(
    val token: String,
    val userId: Long,
    val username: String,
    val role: UserRole,
) {
}