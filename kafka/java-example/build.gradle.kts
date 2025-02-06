plugins {
    java
}

group = "com.yscorp"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
//    implementation("org.apache.avro:avro:1.12.0")
// https://mvnrepository.com/artifact/io.confluent/kafka-avro-serializer
//    implementation("io.confluent:kafka-avro-serializer:7.8.0")
    implementation("org.apache.kafka:kafka-clients:3.9.0")
    implementation("com.github.javafaker:javafaker:1.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    // SLF4J API
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j:slf4j-api:1.7.36")

    // SLF4J Simple Logger
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    implementation("org.slf4j:slf4j-simple:1.7.36")

    // Java Faker
    // https://mvnrepository.com/artifact/com.github.javafaker/javafaker
    implementation("com.github.javafaker:javafaker:1.0.2")

    // PostgreSQL JDBC Driver
    // https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.4.0")

    // Jackson Databind
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.3")

    // Jackson Datatype JSR310 (Java 8 Date and Time API)
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.datatype/jackson-datatype-jsr310
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.3")

}

tasks.withType<Test> {
    useJUnitPlatform()
}
