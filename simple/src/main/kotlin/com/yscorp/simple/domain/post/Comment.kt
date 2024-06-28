package com.yscorp.simple.domain.post

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("comments")
data class Comment(
    @Id val id: Long = 0,
    val postId: Long,
    val userId: Long,
    val content: String,
    @CreatedDate val createdAt: LocalDateTime = LocalDateTime.now(),
    @LastModifiedDate val modifiedAt: LocalDateTime? = null,
)