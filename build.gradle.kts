plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "10.5.2"
  kotlin("plugin.spring") version "2.4.0"
}

configurations {
  implementation {
    exclude(group = "org.springdoc", module = "springdoc-openapi-starter-common")
    exclude(group = "com.fasterxml.jackson.module", module = "jackson-module-kotlin")
  }
}

dependencyCheck {
  suppressionFiles.add("azure-dependency-check-suppress.xml")
}

dependencies {
  implementation("uk.gov.justice.service.hmpps:hmpps-kotlin-spring-boot-starter:2.5.0")
  implementation("org.springframework.boot:spring-boot-starter-webflux")

  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:7.4.0")
  implementation("org.apache.commons:commons-lang3:3.20.0")
  // Needs to match this version https://github.com/microsoft/ApplicationInsights-Java/blob/<version>/dependencyManagement/build.gradle.kts#L16
  // where <version> is the version of application insights pulled in by hmpps-gradle-spring-boot
  // at https://github.com/ministryofjustice/hmpps-gradle-spring-boot/blob/main/src/main/kotlin/uk/gov/justice/digital/hmpps/gradle/configmanagers/AppInsightsConfigManager.kt#L7
  implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.26.1")

  val appinsightsCore = "core:2.6.4"
  implementation("io.micrometer:micrometer-registry-azure-monitor:1.17.0")
  implementation("com.microsoft.azure:applicationinsights-$appinsightsCore")

  testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
  testImplementation("org.wiremock:wiremock:3.13.2")
  testImplementation("org.testcontainers:localstack:1.21.4")
  testImplementation("org.awaitility:awaitility-kotlin:4.3.0")
}

kotlin {
  jvmToolchain(25)
}

tasks {
  withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions.jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25
  }
}
