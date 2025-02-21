package com.yscorp.lgtm.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("posts")
class Post(

    val title: String,
    val content: String,

    val userId: Long,

    ) : BaseEntity() {

    @Id
    val id: Long = 0

}
