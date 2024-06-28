package com.yscorp.simple

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging

class Logger {
}

inline fun <reified T : Any> T.logger(): KLogger {
    val name = T::class.qualifiedName ?: T::class.simpleName ?: "Unknown"
    return KotlinLogging.logger(name)
}
