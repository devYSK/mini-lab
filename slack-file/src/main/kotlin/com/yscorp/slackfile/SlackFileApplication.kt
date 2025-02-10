package com.yscorp.slackfile

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SlackFileApplication

fun main(args: Array<String>) {
    runApplication<SlackFileApplication>(*args)
}
