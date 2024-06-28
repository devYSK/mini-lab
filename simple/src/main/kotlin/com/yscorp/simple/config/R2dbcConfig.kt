package com.yscorp.simple.config

import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.core.DatabaseClient

@EnableR2dbcRepositories
@Configuration
@EnableR2dbcAuditing

class R2dbcConfig(
    private val databaseClient: DatabaseClient
) : ApplicationListener<ApplicationReadyEvent> {

    private val log = org.slf4j.LoggerFactory.getLogger(R2dbcConfig::class.java)

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        // reactor: publisher, subscriber
        databaseClient.sql("SELECT 1").fetch().one()
            .subscribe(
                { success: Map<String?, Any?>? ->
                    log.info(
                        "Initialize r2dbc database connection."
                    )
                }
            ) { error: Throwable? ->
                log.error("Failed to initialize r2dbc database connection.")
                SpringApplication.exit(event.applicationContext, ExitCodeGenerator { -110 })
            }
    }
}

