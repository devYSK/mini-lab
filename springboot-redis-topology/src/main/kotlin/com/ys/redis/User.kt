package com.ys.redis

import java.time.LocalDateTime

class User(
    val id: Long,
    val email: String,
    val password: String,
    val createdAt: LocalDateTime
) {
}