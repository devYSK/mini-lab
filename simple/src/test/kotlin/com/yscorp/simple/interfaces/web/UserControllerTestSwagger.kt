package com.yscorp.simple.interfaces.web

import com.epages.restdocs.apispec.WebTestClientRestDocumentationWrapper
import com.yscorp.simple.domain.user.User
import com.yscorp.simple.domain.user.UserRepository
import com.yscorp.simple.domain.user.UserService
import com.yscorp.simple.restdocs.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.documentationConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.reactive.TransactionalOperator
import org.springframework.web.reactive.function.BodyInserters
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier


@SpringBootTest
@ExtendWith(SpringExtension::class, RestDocumentationExtension::class)
@AutoConfigureRestDocs
@AutoConfigureWebTestClient
class UserControllerTestSwagger {

    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var transactionManager: ReactiveTransactionManager

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var databaseClient: DatabaseClient

    private val transactionalOperator by lazy {
        TransactionalOperator.create(transactionManager)
    }

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider, applicationContext: ApplicationContext) {
        this.webTestClient = WebTestClient.bindToApplicationContext(applicationContext)
            .configureClient()
            .filter(documentationConfiguration(restDocumentation))
            .build()

    }

    @AfterEach
    fun tearDown() {
        databaseClient.sql("DELETE FROM users").fetch().rowsUpdated().block()
    }

    @Test
    fun `사용자 생성 테스트`() {
        val user = User(username = "Test User", password = "password", email = "testuser@example.com")

        val responseMono = webTestClient.post().uri("/api/users")
            .body(BodyInserters.fromPublisher(Mono.just(user), User::class.java))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentationWrapper.document(
                    "create-user",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestBody(
                        "username" type STRING means "사용자 이름",
                        "password" type STRING means "사용자 비밀번호",
                        "email" type STRING means "사용자 이메일",
                        "id" type NUMBER means "사용자 ID",
                        "createdAt" type ARRAY means "생성 일시",
                        "modifiedAt" type ARRAY means "수정 일시" isOptional true
                    ),
                    responseBody(
                        "status" type STRING means "응답 상태",
                        "message" type STRING means "응답 메시지",
                        "data" type OBJECT means "응답 데이터",
                        "data.id" type NUMBER means "생성된 사용자 ID",
                        "data.username" type STRING means "사용자 이름",
                        "data.password" type STRING means "사용자 비밀번호",
                        "data.email" type STRING means "사용자 이메일",
                        "data.createdAt" type STRING means "생성 일시",
                        "data.modifiedAt" type STRING means "수정 일시" isOptional true
                    )
                )
            )
            .toMono()

        StepVerifier.create(responseMono)
            .expectNextCount(1)
            .verifyComplete()
    }


    @Test
    fun `사용자 조회 테스트`() {
        val userMono =
            userService.createUser(User(username = "Test User", password = "password", email = "testuser@example.com"))

        val user = userMono.block()!!

        val responseMono = webTestClient.get().uri("/api/users/{id}", user.id)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentationWrapper.document(
                    "get-user",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("id").description("사용자 ID")
                    ),
                    responseBody(
                        "status" type STRING means "응답 상태",
                        "message" type STRING means "응답 메시지",
                        "data" type OBJECT means "응답 데이터",
                        "data.id" type NUMBER means "사용자 ID",
                        "data.username" type STRING means "사용자 이름",
                        "data.password" type STRING means "사용자 비밀번호",
                        "data.email" type STRING means "사용자 이메일",
                        "data.createdAt" type STRING means "생성 일시",
                        "data.modifiedAt" type STRING means "수정 일시"
                    )
                )
            )
            .toMono()

        StepVerifier.create(responseMono)
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun `사용자 업데이트 테스트`() {
        val userMono = userService.createUser(User(username = "Test User", password = "password", email = "testuser@example.com"))

        val user = userMono.block()!!

        val updatedUser = user.copy(username = "Updated User")

        val responseMono = webTestClient.put().uri("/api/users/{id}", user.id)
            .body(BodyInserters.fromPublisher(Mono.just(updatedUser), User::class.java))
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith(
                WebTestClientRestDocumentationWrapper.document(
                    "update-user",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("id").description("사용자 ID")
                    ),
                    requestBody(
                        "username" type STRING means "사용자 이름",
                        "password" type STRING means "사용자 비밀번호",
                        "email" type STRING means "사용자 이메일",
                        "id" type NUMBER means "사용자 ID",
                        "createdAt" type ARRAY means "생성 일시",
                        "modifiedAt" type ARRAY means "수정 일시" isOptional true
                    ),
                    responseBody(
                        "status" type STRING means "응답 상태",
                        "message" type STRING means "응답 메시지",
                        "data" type OBJECT means "응답 데이터",
                        "data.id" type NUMBER means "사용자 ID",
                        "data.username" type STRING means "변경된 사용자 이름",
                        "data.password" type STRING means "사용자 비밀번호",
                        "data.email" type STRING means "사용자 이메일",
                        "data.createdAt" type STRING means "생성 일시",
                        "data.modifiedAt" type STRING means "수정 일시"
                    )
                )
            ).toMono()


        StepVerifier.create(responseMono)
            .expectNextCount(1)
            .verifyComplete()
    }

    @Test
    fun `사용자 목록 조회 테스트`() {
        val users = listOf(
            User(username = "Test User 1", password = "password1", email = "testuser1@example.com"),
            User(username = "Test User 2", password = "password2", email = "testuser2@example.com")
        )

        val saveMono = userRepository.saveAll(users).then()

        StepVerifier.create(saveMono)
            .verifyComplete()

        val responseMono = webTestClient.get().uri("/api/users")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .consumeWith { response ->
                println("Request: GET /api/users")
                println("Response: ${String(response.responseBody!!)}")
            }
            .consumeWith(
                WebTestClientRestDocumentationWrapper.document(
                    "get-users",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").description("응답 데이터").type(JsonFieldType.ARRAY),
                        fieldWithPath("data[].id").description("사용자 ID").type(JsonFieldType.NUMBER),
                        fieldWithPath("data[].username").description("사용자 이름").type(JsonFieldType.STRING),
                        fieldWithPath("data[].password").description("사용자 비밀번호").type(JsonFieldType.STRING),
                        fieldWithPath("data[].email").description("사용자 이메일").type(JsonFieldType.STRING),
                        fieldWithPath("data[].createdAt").description("생성 일시").type(JsonFieldType.STRING).optional(),
                        fieldWithPath("data[].modifiedAt").description("수정 일시").type(JsonFieldType.STRING).optional()
                    )
                )
            )
            .toMono()

        StepVerifier.create(responseMono)
            .expectNextCount(1)
            .verifyComplete()
    }

}