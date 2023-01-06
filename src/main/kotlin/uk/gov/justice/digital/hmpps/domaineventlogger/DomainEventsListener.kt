package uk.gov.justice.digital.hmpps.domaineventlogger

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.fasterxml.jackson.module.kotlin.readValue
import com.microsoft.applicationinsights.TelemetryClient
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class DomainEventsListener(
  private val objectMapper: ObjectMapper,
  private val telemetryClient: TelemetryClient
) {

  private companion object {
    val log: Logger = LoggerFactory.getLogger(this::class.java)
  }

  @SqsListener("prisoner", factory = "hmppsQueueContainerFactoryProxy")
  fun onDomainEventReceived(rawMessage: String) {
    log.info("Received message {}", rawMessage)
    try {
      val sqsMessage: SQSMessage = objectMapper.readValue(rawMessage)
      val event = translateMap(objectMapper.readValue(sqsMessage.Message))
      telemetryClient.trackEvent(
        sqsMessage.MessageAttributes.eventType.Value,
        event,
        null
      )
    } catch (exception: Exception) {
      log.error("Received malformed domain event message :$rawMessage", exception)
    }
  }

  /* Domain events have top level attributes and some optional nested objects - eg AdditionalInformation. */
  fun translateMap(rawMap: Map<String, Any>): Map<String, String> {
    val mapNested = rawMap.filter { it.value is Map<*, *> } as Map<String, Map<*, *>>
    // for nested properties use top level key and nested key seperated by a full stop
    val flattenedNestedEntries: List<Map.Entry<String, String>> =
      mapNested.map { (key, value) -> value.entries.associate { "$key.${it.key}" to it.value.toString() } }
        .flatMap { it.entries }
    return rawMap.filter { it.value !is Map<*, *> }.mapValues { it.value.toString() }.toMutableMap()
      .plus(flattenedNestedEntries.map { it.toPair() }.toMap())
  }

  @JsonNaming(value = PropertyNamingStrategies.UpperCamelCaseStrategy::class)
  data class SQSMessage(val Type: String, val Message: String, val MessageId: String, val MessageAttributes: MessageAttributes)
  @JsonNaming(value = PropertyNamingStrategies.UpperCamelCaseStrategy::class)
  data class EventType(val Value: String)
  data class MessageAttributes(val eventType: EventType)
}
