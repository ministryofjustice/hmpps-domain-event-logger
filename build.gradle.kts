plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "9.1.1"
  kotlin("plugin.spring") version "2.2.20"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:5.5.0-beta")
  implementation("org.springframework.boot:spring-boot-starter-webflux")

  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:5.4.11")
  implementation("org.apache.commons:commons-lang3:3.18.0")
  implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.18.1")

  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.34")
  testImplementation("org.wiremock:wiremock:3.13.1")
  testImplementation("org.mockito:mockito-inline:5.2.0")
  testImplementation("org.testcontainers:localstack:1.21.3")
  testImplementation("org.awaitility:awaitility-kotlin:4.3.0")
}

kotlin {
  jvmToolchain(21)
  compilerOptions {
    freeCompilerArgs.addAll("-Xjvm-default=all", "-Xwhen-guards", "-Xannotation-default-target=param-property")
  }
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21
  }
}
