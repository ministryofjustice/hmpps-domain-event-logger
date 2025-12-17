package uk.gov.justice.digital.hmpps.domaineventlogger.integration

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import software.amazon.awssdk.services.sqs.model.PurgeQueueRequest
import uk.gov.justice.digital.hmpps.domaineventlogger.integration.LocalStackContainer.setLocalStackProperties
import uk.gov.justice.hmpps.sqs.HmppsQueue
import uk.gov.justice.hmpps.sqs.HmppsQueueService

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@AutoConfigureWebTestClient
abstract class SqsIntegrationTestBase {
  @Autowired
  lateinit var webTestClient: WebTestClient

  @Autowired
  private lateinit var hmppsQueueService: HmppsQueueService

  internal val registersQueue by lazy { hmppsQueueService.findByQueueId("prisoner") as HmppsQueue }

  internal val awsSqsClient by lazy { registersQueue.sqsClient }
  internal val awsSqsDlqClient by lazy { registersQueue.sqsDlqClient }
  internal val queueUrl by lazy { registersQueue.queueUrl }
  internal val dlqUrl by lazy { registersQueue.dlqUrl }

  @BeforeEach
  fun cleanQueue() {
    awsSqsClient.purgeQueue(PurgeQueueRequest.builder().queueUrl(queueUrl).build()).get()
    awsSqsDlqClient?.purgeQueue(PurgeQueueRequest.builder().queueUrl(dlqUrl).build())?.get()
  }

  companion object {
    private val localStackContainer = LocalStackContainer.instance

    @JvmStatic
    @DynamicPropertySource
    fun testcontainers(registry: DynamicPropertyRegistry) {
      localStackContainer?.also { setLocalStackProperties(it, registry) }
    }
  }
}
