package com.yscorp.simple.domain.user

import org.aspectj.lang.annotation.After
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.ReactiveTransaction
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import reactor.core.CorePublisher
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier

@TestPropertySource(locations = ["classpath:application-test.yml"])
@SpringBootTest
class UserServiceTests {

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var transactionManager: ReactiveTransactionManager

    @Autowired
    private lateinit var databaseClient: DatabaseClient

//    private val transactionalOperator by lazy {
//        TransactionalOperator.create(transactionManager)
//    }
//
    @Autowired
    private lateinit var transactionStepVerifier: TransactionStepVerifier

    @Autowired
    private lateinit var transactionOperator: TransactionalOperator

    @BeforeEach
    fun setup() {

        // 스키마가 없으면 V1__initial_schema.sql 실행
        databaseClient.sql("SELECT 1 FROM information_schema.tables WHERE table_name = 'users'")
            .fetch()
            .rowsUpdated()
            .flatMap {
                if (it == 0L) {
                    val resource = ClassPathResource("db/migration/V1__initial_schema.sql")
                    val populator = ResourceDatabasePopulator(resource)
                    populator.populate(databaseClient.connectionFactory)
                } else {
                    Mono.empty()
                }
            }
            .block()
    }

    @AfterEach
    fun tearDown() {
        databaseClient.sql("DELETE FROM users").fetch().rowsUpdated().block()
    }

    @Test
    fun `사용자 생성`() {
        val user = User(username = "john_doe", password = "password123", email = "john.doe@example.com")

        transactionOperator.execute {
            userService.createUser(user)
        }.`as`(StepVerifier::create)
            .assertNext {
                assertThat(it.id).isNotNull()
                assertThat(it.username).isEqualTo("john_doe")
                assertThat(it.password).isEqualTo("password123")
                assertThat(it.email).isEqualTo("john.doe@example.com")
            }
            .verifyComplete()
    }

    @Test
    fun `사용자 아이디로 조회`() {
        val user = userRepository.save(User(username = "john_doe", password = "password123", email = "john.doe@example.com")).block()!!

        transactionOperator.execute {
            userService.getUser(user.id)
        }.`as`(StepVerifier::create)
            .assertNext {
                assertThat(it.id).isEqualTo(user.id)
                assertThat(it.username).isEqualTo("john_doe")
                assertThat(it.password).isEqualTo("password123")
                assertThat(it.email).isEqualTo("john.doe@example.com")
            }
            .verifyComplete()
    }

    @Test
    fun `사용자 아이디로 조회 실패`() {
        transactionOperator.execute {
            userService.getUser(999L)
        }.`as`(StepVerifier::create)
            .expectErrorMatches { it.message == "User not found" }
            .verify()
    }

    @Test
    fun `사용자 업데이트`() {
        val user = userRepository.save(User(username = "john_doe", password = "password123", email = "john.doe@example.com")).block()!!
        val updatedUser = user.copy(username = "john_updated", email = "john.updated@example.com")

        transactionOperator.execute {
            userService.updateUser(updatedUser)
        }.`as`(StepVerifier::create)
            .assertNext {
                assertThat(it.id).isEqualTo(user.id)
                assertThat(it.username).isEqualTo("john_updated")
                assertThat(it.password).isEqualTo("password123")
                assertThat(it.email).isEqualTo("john.updated@example.com")
            }
            .verifyComplete()
    }

//    @Test
//    fun `사용자 삭제`() {
//        val user = userRepository.save(User(username = "john_doe", password = "password123", email = "john.doe@example.com")).block()!!
//
//        transactionOperator.execute {
//            userService.deleteUser(user.id).toMono()
//        }.`as`(StepVerifier::create)
//            .expectNext(Unit)  // 삭제 메서드가 Unit을 반환하는 경우 이를 기대
//            .verifyComplete()
//
//        transactionOperator.execute {
//            userRepository.findById(user.id)
//        }.`as`(StepVerifier::create)
//            .expectNextCount(0) // 사용자가 삭제되었기 때문에 결과가 없어야 합니다
//            .verifyComplete()
//    }

    @Test
    fun `모든 사용자 조회`() {
        val users = listOf(
            User(username = "john_doe", password = "password123", email = "john.doe@example.com"),
            User(username = "jane_doe", password = "password456", email = "jane.doe@example.com")
        )
        userRepository.saveAll(users).collectList().block()

        transactionOperator.execute {
            userService.getUsers().collectList()
        }.`as`(StepVerifier::create)
            .assertNext {
                assertThat(it).hasSize(2)
                assertThat(it).anyMatch { user -> user.username == "john_doe" && user.email == "john.doe@example.com" }
                assertThat(it).anyMatch { user -> user.username == "jane_doe" && user.email == "jane.doe@example.com" }
            }
            .verifyComplete()
    }
}

@Component
class TransactionStepVerifier(
    private val transactionalOperator: TransactionalOperator,
) {
    fun <T> create(publisher: Mono<T>) = publisher
        .`as` { withRollback(publisher) }
        .`as` { StepVerifier.create(it) }

    fun <T> create(publisher: Flux<T>) = publisher
        .`as` { withRollback(publisher) }
        .`as` { StepVerifier.create(it) }

    private fun <T> withRollback(mono: Mono<T>) = setupRollback(mono).next()

    private fun <T> withRollback(flux: Flux<T>) = setupRollback(flux)

    private fun <T> setupRollback(publisher: CorePublisher<T>) =
        transactionalOperator.execute {
            it.setRollbackOnly()
            publisher
        }
}


@Component
class Transaction @Autowired protected constructor(operator: TransactionalOperator?) {
    init {
        Companion.operator = operator
    }

    companion object {
        private var operator: TransactionalOperator? = null
        fun <T> withRollBack(publisher: Mono<T?>?): Mono<T?> {
            return operator!!.execute { tx: ReactiveTransaction ->
                tx.setRollbackOnly()
                publisher!!
            }.next()
        }

        fun <T> withRollBack(publisher: Flux<T?>?): Flux<T?> {
            return operator!!.execute { tx: ReactiveTransaction ->
                tx.setRollbackOnly()
                publisher!!
            }
        }
    }
}