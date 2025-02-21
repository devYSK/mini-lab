package com.yscorp.lgtm.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
class User(
    var name: String,
    var username: String,
    var password: String,

    val role: UserRole = UserRole.USER,

    id: Long? = null,

) : BaseEntity() {


    @Id
    val id: Long = id ?: 0

}