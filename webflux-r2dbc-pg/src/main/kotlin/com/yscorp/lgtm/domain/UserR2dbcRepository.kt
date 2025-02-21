package com.yscorp.lgtm.domain

import org.springframework.data.r2dbc.repository.R2dbcRepository

interface UserR2dbcRepository : R2dbcRepository<User, Long> {

}