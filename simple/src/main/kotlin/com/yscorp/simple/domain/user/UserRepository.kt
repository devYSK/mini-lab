package com.yscorp.simple.domain.user

import org.springframework.data.r2dbc.repository.R2dbcRepository

interface UserRepository : R2dbcRepository<User, Long> {

}
