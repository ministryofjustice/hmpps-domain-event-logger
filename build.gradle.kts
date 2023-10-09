plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "5.5.0"
  kotlin("plugin.spring") version "1.9.10"
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
  implementation("org.springframework.boot:spring-boot-starter-cache")

  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:2.1.1")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
  implementation("org.apache.commons:commons-lang3:3.13.0")
  implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:1.30.0")

  testImplementation("io.swagger.parser.v3:swagger-parser:2.1.16")
  testImplementation("org.wiremock:wiremock:3.2.0")
  testImplementation("org.mockito:mockito-inline:5.2.0")
  testImplementation("org.testcontainers:localstack:1.19.1")
  testImplementation("org.awaitility:awaitility-kotlin:4.2.0")
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(20))
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
      jvmTarget = "20"
    }
  }

  test {
    // required for jjwt 0.12 - see https://github.com/jwtk/jjwt/issues/849
    jvmArgs("--add-exports", "java.base/sun.security.util=ALL-UNNAMED")
  }
}
