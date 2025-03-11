plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "7.1.3"
  kotlin("plugin.spring") version "2.1.10"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:1.4.0-beta")
  implementation("org.springframework.boot:spring-boot-starter-webflux")

  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:5.3.1")
  implementation("org.apache.commons:commons-lang3:3.17.0")
  implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.12.0")

  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.25")
  testImplementation("org.wiremock:wiremock:3.12.0")
  testImplementation("org.mockito:mockito-inline:5.2.0")
  testImplementation("org.testcontainers:localstack:1.20.5")
  testImplementation("org.awaitility:awaitility-kotlin:4.3.0")
}

kotlin {
  jvmToolchain(21)
  compilerOptions {
    freeCompilerArgs.add("-Xwhen-guards")
  }
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
  }
}
