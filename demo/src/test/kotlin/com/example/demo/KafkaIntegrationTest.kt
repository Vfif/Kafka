package com.example.demo

import com.example.demo.config.KafkaConfig.CLIENT_TOPIC
import com.example.demo.domain.Client
import com.example.demo.serializer.ClientDeserializer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.LongDeserializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KafkaIntegrationTest(
    @Autowired val restTemplate: TestRestTemplate
) {
    companion object {
        @Container
        private val kafkaContainer: KafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.4"))
    }

    private lateinit var kafkaConsumer: KafkaConsumer<Long, Client>

    @BeforeEach
    fun setup() {
        val consumerProps = Properties().apply {
            this[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaContainer.bootstrapServers
            this[ConsumerConfig.GROUP_ID_CONFIG] = "client-group"
            this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = LongDeserializer::class.java.name
            this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = ClientDeserializer::class.java.name
            this[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        }
        kafkaConsumer = KafkaConsumer(consumerProps)
        kafkaConsumer.subscribe(listOf(CLIENT_TOPIC))
    }

    @AfterEach
    fun tearDown() {
        kafkaConsumer.close()
        kafkaContainer.stop()
    }

    @Test
    fun `should send message to Kafka when API is called`() {
        val client = Client(
            id = 11L,
            email = "user@gmail.com",
            firstName = "User",
            lastName = "Admin"
        )

        val response = restTemplate.exchange("/clients", POST, HttpEntity<Client>(client), Client::class.java)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(client, response.body)

        val records = kafkaConsumer.poll(Duration.of(5, ChronoUnit.SECONDS))
        val kafkaMessage = records.records(CLIENT_TOPIC).firstOrNull()?.value()

        assertEquals(client, kafkaMessage)
    }
}
