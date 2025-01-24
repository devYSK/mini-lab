package com.ys.springbootdistributedlock.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackages = ["com.ys"])
class Config {
}

fun <T : Any> T.logger(): Logger = LoggerFactory.getLogger(javaClass)
