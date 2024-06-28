import io.swagger.v3.oas.annotations.servers.Server
import io.swagger.v3.oas.models.info.Contact
import org.gradle.internal.impldep.org.bouncycastle.asn1.x500.style.RFC4519Style.title


plugins {
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    id("com.epages.restdocs-api-spec") version "0.18.2"
}

buildscript {
    // 2. ext 블록 대신에 extra.properties를 사용합니다.
    extra["restdocsApiSpecVersion"] = "0.18.2"
}

extra["snippetsDir"] = file("build/generated-snippets")


val asciidoctorExt: Configuration by configurations.creating
val snippetsDir by extra { file("build/generated-snippets") }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "spring-boot-starter-web")
        exclude(module = "spring-webmvc")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("io.mockk:mockk:1.13.2")
    //
    implementation("com.github.f4b6a3:tsid-creator:5.2.6")

    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor")

    // swagger
//    testImplementation("com.epages:restdocs-api-spec-mockmvc:0.18.2") // 3
    // https://mvnrepository.com/artifact/org.springdoc/springdoc-openapi-webflux-ui
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0")
// https://mvnrepository.com/artifact/com.epages/restdocs-api-spec-webtestclient
    // https://mvnrepository.com/artifact/com.epages/restdocs-api-spec

    // 제외하지 않으면 mvc 관련 라이브러리가 같이 포함된다. 때문에 webhandler가 없다는 에러가 발생한다.
    implementation("com.epages:restdocs-api-spec:0.19.2") {
        exclude(module = "spring-boot-starter-web")
        exclude(module = "spring-webmvc")
    }
    implementation("com.epages:restdocs-api-spec-webtestclient:0.19.2") {
        exclude(module = "spring-boot-starter-web")
        exclude(module = "spring-webmvc")
    }

}

tasks.asciidoctor {
    inputs.dir(snippetsDir)
    sourceDir("src/test/resources/api")
    configurations(asciidoctorExt.name)
    dependsOn(tasks.test)

    baseDirFollowsSourceFile()

    doFirst {
        delete {
            file("src/main/resources/static/docs")
        }
    }
}

tasks.register("copyHtml", Copy::class) {
    dependsOn(tasks.asciidoctor)
    from(file("build/docs/asciidoc/"))
    into(file("src/main/resources/static/docs"))
}

tasks.build {
    dependsOn(tasks.getByName("copyHtml"))
}

tasks.bootJar {
    dependsOn(tasks.asciidoctor)
    dependsOn(tasks.getByName("copyHtml"))
}


// OpenAPI Generator 플러그인 확장을 가져와서 사용한다.


openapi3.apply {

    // Server 설정
    setServer("http://production-api-server-url.com")

    // 기타 설정
//    outputFileNamePrefix = "openapi"
    title = "Post Service API"
    description = "Post Service API description"
    version = "1.0.0"
    format = "yaml"

    outputDirectory = "build/resources/main/static/docs"
}
