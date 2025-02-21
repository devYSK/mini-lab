package com.yscorp.lgtm.config

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(
)
@ConfigurationPropertiesScan(value = ["com"])
class PropertyConfig {
}