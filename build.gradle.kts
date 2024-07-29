plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "6.0.2-beta"
  kotlin("plugin.spring") version "2.0.0"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:1.0.2-beta-3")
  implementation("org.springframework.boot:spring-boot-starter-webflux")

  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:4.1.0")
  implementation("org.apache.commons:commons-lang3:3.15.0")
  implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.6.0")

  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.22")
  testImplementation("org.wiremock:wiremock:3.9.1")
  testImplementation("org.mockito:mockito-inline:5.2.0")
  testImplementation("org.testcontainers:localstack:1.20.0")
  testImplementation("org.awaitility:awaitility-kotlin:4.2.1")
}

kotlin {
  jvmToolchain(21)
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
  }
}
