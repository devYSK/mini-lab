
dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.redisson:redisson-spring-boot-starter:3.23.3")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-hibernate5-jakarta")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")



    implementation("io.netty:netty-resolver-dns-native-macos:4.1.94.Final:osx-aarch_64")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    // crudrepository, redis hash 용도
    implementation("jakarta.persistence:jakarta.persistence-api")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("it.ozimov:embedded-redis:0.7.2")
    testImplementation("com.redis.testcontainers:testcontainers-redis-junit:1.6.4")
}
