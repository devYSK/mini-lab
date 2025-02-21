package com.yscorp.lgtm.config.security

import org.springframework.security.core.AuthenticationException

class UnAuthenticationException : AuthenticationException {

    constructor(message: String) : super(message)

    constructor(message: String, ex: Exception) : super(message, ex)
}
