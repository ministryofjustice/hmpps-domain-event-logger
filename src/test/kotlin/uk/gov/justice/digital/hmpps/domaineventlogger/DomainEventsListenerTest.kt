package uk.gov.justice.digital.hmpps.domaineventlogger

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.microsoft.applicationinsights.TelemetryClient
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.check
import org.mockito.kotlin.eq
import org.mockito.kotlin.isNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions

internal class PrisonerDomainEventsListenerTest {
  private val objectMapper: ObjectMapper = objectMapper()
  private val telemetryClient: TelemetryClient = mock()

  private val listener =
    DomainEventsListener(
      objectMapper,
      telemetryClient
    )

  @Test
  internal fun `will log domain events with name of eventType`() {
    listener.onDomainEventReceived(
      rawMessage = incentiveCreatedMessage()
    )

    verify(telemetryClient).trackEvent(
      eq("incentives.iep-review.inserted"),
      check {
        assertThat(it["source"]).isEqualTo("nomis")
        assertThat(it["additionalInformation.id"]).isEqualTo("123")
        assertThat(it["additionalInformation.property2"]).isEqualTo("hello")
      },
      isNull()
    )
  }

  @Test
  internal fun `can handle unexpected data`() {
    listener.onDomainEventReceived(
      rawMessage = poorQualityMessage()
    )

    verify(telemetryClient).trackEvent(
      eq("low-quality-event"),
      check {
        assertThat(it["source"]).isEqualTo("")
        assertThat(it["additionalInformation"]).isNull()
        assertThat(it["nestedMap.emptyProperty"]).isEqualTo("")
      },
      isNull()
    )
  }

  @Test
  internal fun `eventType is missing`() {
    listener.onDomainEventReceived(
      rawMessage = noEventTypeMessage()
    )
    verifyNoInteractions(telemetryClient)
  }
}

private fun objectMapper(): ObjectMapper {
  return ObjectMapper()
    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    .registerModule(JavaTimeModule())
    .registerKotlinModule()
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}

fun incentiveCreatedMessage() = """
      {
        "Type": "Notification", 
        "MessageId": "48e8a79a-0f43-4338-bbd4-b0d745f1f8ec", 
        "Token": null, 
        "TopicArn": "arn:aws:sns:eu-west-2:000000000000:hmpps-domain-events", 
        "Message": "{\"eventType\":\"incentives.iep-review.inserted\", \"source\":\"nomis\",\"additionalInformation\": {\"id\":\"123\", \"property2\":\"hello\"}}",
        "MessageAttributes" : {
        "eventType" : {"Type":"String","Value":"incentives.iep-review.inserted"}},
        "SubscribeURL": null, 
        "Timestamp": "2021-03-05T11:23:56.031Z", 
        "SignatureVersion": "1", 
        "Signature": "EXAMPLEpH+..", 
        "SigningCertURL": "https://sns.us-east-1.amazonaws.com/SimpleNotificationService-0000000000000000000000.pem"}      
""".trimIndent()

fun poorQualityMessage() = """
      {
        "Type": "Notification", 
        "MessageId": "48e8a79a-0f43-4338-bbd4-b0d745f1f8ec", 
        "Token": null, 
        "TopicArn": "arn:aws:sns:eu-west-2:000000000000:hmpps-domain-events", 
        "Message": "{\"source\":\"\",\"additionalInformation\": {},\"nestedMap\": {\"id\":\"456\", \"emptyProperty\":\"\"},\"multipleNestedMaps\": {\"mapInternal\": {\"anotherNestedMap\":\"\"}}}",
        "MessageAttributes" : {
        "eventType" : {"Type":"String","Value":"low-quality-event"}},
        "SubscribeURL": null, 
        "Timestamp": "2021-03-05T11:23:56.031Z", 
        "SignatureVersion": "1", 
        "Signature": "EXAMPLEpH+..", 
        "SigningCertURL": "https://sns.us-east-1.amazonaws.com/SimpleNotificationService-0000000000000000000000.pem"}      
""".trimIndent()

fun noEventTypeMessage() = """
      {
        "Type": "Notification", 
        "MessageId": "48e8a79a-0f43-4338-bbd4-b0d745f1f8ec", 
        "Token": null, 
        "TopicArn": "arn:aws:sns:eu-west-2:000000000000:hmpps-domain-events", 
        "Message": "{\"source\":\"\",\"additionalInformation\": {},\"nestedMap\": {\"id\":\"456\", \"emptyProperty\":\"\"},\"multipleNestedMaps\": {\"mapInternal\": {\"anotherNestedMap\":\"\"}}}",
        "MessageAttributes" : {},
        "SubscribeURL": null, 
        "Timestamp": "2021-03-05T11:23:56.031Z", 
        "SignatureVersion": "1", 
        "Signature": "EXAMPLEpH+..", 
        "SigningCertURL": "https://sns.us-east-1.amazonaws.com/SimpleNotificationService-0000000000000000000000.pem"}      
""".trimIndent()
