package uk.gov.justice.digital.hmpps.domaineventlogger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication()
class HmppsDomainEventLogger

fun main(args: Array<String>) {
  runApplication<HmppsDomainEventLogger>(*args)
}
