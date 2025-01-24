package com.ys.springbootnamedlockjpa

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@EnableJpaRepositories
@SpringBootApplication
class SpringBootNamedLockJpaApplication

fun main(args: Array<String>) {
    runApplication<SpringBootNamedLockJpaApplication>(*args)
}
