plugins {
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

group = "com.yscorp"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
}
dependencies {
    implementation("org.apache.avro:avro:1.12.0")
// https://mvnrepository.com/artifact/io.confluent/kafka-avro-serializer
    implementation("io.confluent:kafka-avro-serializer:7.8.0")

    implementation("org.apache.kafka:kafka-clients:3.9.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

avro {
    setCreateSetters(false) // 필요시 Setter 생성 비활성화
    stringType = "String" // 문자열 타입 설정
    fieldVisibility = "PRIVATE" // 필드 가시성 설정
}

tasks.withType<com.github.davidmc24.gradle.plugin.avro.GenerateAvroJavaTask> {
    setOutputDir(file("src/main/avro/generated-avro")) // 출력 디렉토리 설정
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}