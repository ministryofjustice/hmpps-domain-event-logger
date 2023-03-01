plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.1.1-beta-2"
  kotlin("plugin.spring") version "1.8.10"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

repositories {
  maven { url = uri("https://repo.spring.io/milestone") }
  mavenCentral()
}
dependencies {
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:2.0.0-beta-10")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
  implementation("org.apache.commons:commons-lang3:3.12.0")
  implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:1.23.0")

  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.12")
  testImplementation("com.github.tomakehurst:wiremock-jre8-standalone:2.35.0")
  testImplementation("org.mockito:mockito-inline:5.1.1")
  testImplementation("org.testcontainers:localstack:1.17.6")
  testImplementation("org.awaitility:awaitility-kotlin:4.2.0")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(19))
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "19"
    }
  }
}
