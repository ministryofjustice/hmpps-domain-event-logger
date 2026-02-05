package uk.gov.justice.digital.hmpps.domaineventlogger

import com.microsoft.applicationinsights.TelemetryClient
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue
import uk.gov.justice.hmpps.sqs.SnsMessage

@Service
class DomainEventsListener(
  private val jsonMapper: JsonMapper,
  private val telemetryClient: TelemetryClient,
) {

  private companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @SqsListener("prisoner", factory = "hmppsQueueContainerFactoryProxy")
  fun onDomainEventReceived(rawMessage: String) {
    log.info("Received message {}", rawMessage)
    try {
      val sqsMessage: SnsMessage = jsonMapper.readValue(rawMessage)
      val event = translateMap(jsonMapper.readValue(sqsMessage.message)) + ("rawMessage" to rawMessage)
      telemetryClient.trackEvent(
        sqsMessage.messageAttributes.eventType,
        event,
        null,
      )
    } catch (exception: Exception) {
      log.error("Received malformed domain event message :$rawMessage", exception)
    }
  }

  /* Domain events have top level attributes and some optional nested objects - eg AdditionalInformation. */
  fun translateMap(rawMap: Map<String, Any?>): Map<String, String> {
    val mapNested = rawMap.filter { it.value is Map<*, *> } as Map<String, Map<*, *>>
    // for nested properties use top level key and nested key seperated by a full stop
    val flattenedNestedEntries: List<Map.Entry<String, String>> =
      mapNested.map { (key, value) -> value.entries.filter { it.value != null }.associate { "$key.${it.key}" to it.value.toString() } }
        .flatMap { it.entries }
    return rawMap.filter { it.value != null && it.value !is Map<*, *> }.mapValues { it.value.toString() }.toMutableMap()
      .plus(flattenedNestedEntries.associate { it.toPair() })
  }
}
