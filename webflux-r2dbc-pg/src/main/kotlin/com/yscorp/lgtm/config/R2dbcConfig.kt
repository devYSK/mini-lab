package com.yscorp.lgtm.config

import com.yscorp.lgtm.common.logger
import com.yscorp.webflux.config.R2dbcProperties
import io.github.oshai.kotlinlogging.KotlinLogging
import io.r2dbc.spi.ConnectionFactory
import org.springframework.boot.ExitCodeGenerator
import org.springframework.boot.SpringApplication
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicInteger
import org.springframework.transaction.support.TransactionSynchronizationManager

// 테스트 방법 https://steady-coding.tistory.com/640

@Component
@EnableR2dbcRepositories
@EnableR2dbcAuditing
class R2dbcConfig(
    private val databaseClient: DatabaseClient,
    private val r2dbcProperties: R2dbcProperties,
) : ApplicationListener<ApplicationReadyEvent> {

    private val log = KotlinLogging.logger { }

    override fun onApplicationEvent(event: ApplicationReadyEvent) {
        // reactor: publisher, subscriber
        databaseClient.sql("SELECT 1").fetch().one()
            .subscribe(
                {
                    log.info { "Initialize r2dbc database connection." }
                },
                {
                    log.error { "Failed to initialize r2dbc database connection." }
                    SpringApplication.exit(event.applicationContext, ExitCodeGenerator { -110 })
                }
            )
    }

    @Bean
    fun writeConnectionFactory(): ConnectionFactory {
        val writeConfig = r2dbcProperties.write
        return ConnectionFactoryBuilder.withUrl(writeConfig.url)
            .username(writeConfig.username)
            .password(writeConfig.password)
            .build()
    }

    @Bean
    fun readConnectionFactories(): List<ConnectionFactory> {
        return r2dbcProperties.read.replicas.map { replicaConfig ->
            ConnectionFactoryBuilder.withUrl(replicaConfig.url)
                .username(replicaConfig.username)
                .password(replicaConfig.password)
                .build()
        }
    }

    @Bean
    fun routingConnectionFactory(
        writeConnectionFactory: ConnectionFactory,
        readConnectionFactories: List<ConnectionFactory>,
    ): ConnectionFactory {
        return RoutingConnectionFactory(writeConnectionFactory, readConnectionFactories)
    }

    @Bean
    fun databaseClient(routingConnectionFactory: ConnectionFactory): DatabaseClient {
        return DatabaseClient.create(routingConnectionFactory)
    }

}

class RoutingConnectionFactory(
    private val writeConnectionFactory: ConnectionFactory,
    private val readConnectionFactories: List<ConnectionFactory>,
) : AbstractRoutingConnectionFactory() {

    private val readIndex = AtomicInteger(0)
    private val logger = logger()

    override fun determineCurrentLookupKey(): Mono<Any> {
        return Mono.defer {
            val isReadOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly()
            if (isReadOnly) {
                logger.info("Transaction is READ-ONLY: Using read connection")
                Mono.just("read")
            } else {
                logger.info("Transaction is WRITE: Using write connection")
                Mono.just("write")
            }
        }
    }

    override fun determineTargetConnectionFactory(): Mono<ConnectionFactory> {
        return determineCurrentLookupKey().map { lookupKey ->
            if (lookupKey == "write") {
                writeConnectionFactory
            } else {
                // 라운드 로빈 방식으로 읽기 ConnectionFactory 선택
                val index = readIndex.getAndIncrement() % readConnectionFactories.size
                logger.info("Using read connection at index: $index")
                readConnectionFactories[index]
            }
        }
    }

}