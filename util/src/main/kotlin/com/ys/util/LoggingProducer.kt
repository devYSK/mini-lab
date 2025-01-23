package com.ys.util

interface LoggingProducer {

    fun sendMessage(key: String, value: String)

}