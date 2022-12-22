package uk.gov.justice.digital.hmpps.hmppsdomaineventlogger

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication()
class HmppsDomainEventLogger

fun main(args: Array<String>) {
  runApplication<HmppsDomainEventLogger>(*args)
}
