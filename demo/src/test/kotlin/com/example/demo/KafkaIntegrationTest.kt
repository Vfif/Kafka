package com.example.demo

import com.example.demo.config.KafkaConfig.CLIENT_TOPIC
import com.example.demo.config.KafkaConfig.TRANSACTION_TOPIC
import com.example.demo.domain.Client
import com.example.demo.domain.Transaction
import com.example.demo.domain.TransactionType
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.test.assertNotNull

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KafkaIntegrationTest(
    @Autowired val restTemplate: TestRestTemplate,
    @Autowired private val objectMapper: ObjectMapper
) {
    companion object {
        @Container
        @JvmStatic
        private val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.4"))

        @DynamicPropertySource
        @JvmStatic
        fun kafkaProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
        }

        private lateinit var kafkaConsumer: KafkaConsumer<Long, String>

        @BeforeAll
        @JvmStatic
        fun setup() {
            val consumerProps = Properties().apply {
                this[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaContainer.bootstrapServers
                this[ConsumerConfig.GROUP_ID_CONFIG] = "client-group"
                this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = LongDeserializer::class.java.name
                this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
                this[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
            }
            kafkaConsumer = KafkaConsumer(consumerProps)
            kafkaConsumer.subscribe(listOf(CLIENT_TOPIC, TRANSACTION_TOPIC))
        }

        @AfterAll
        @JvmStatic
        fun tearDown() {
            kafkaConsumer.close()
            kafkaContainer.stop()
        }
    }


    @Test
    fun `should send client message to Kafka when API is called`() {
        val client = Client(
            id = 11L,
            email = "user@gmail.com",
            firstName = "User",
            lastName = "Admin"
        )

        val response =
            restTemplate.exchange("/clients", POST, HttpEntity<Client>(client), Unit::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        val records = kafkaConsumer.poll(Duration.of(5, ChronoUnit.SECONDS))
        val kafkaMessage = records.records(CLIENT_TOPIC).firstOrNull()
        assertNotNull(kafkaMessage)

        val key = kafkaMessage.key()
        assertEquals(client.id, key)

        val value = kafkaMessage.value().let { objectMapper.readValue(it, Client::class.java) }
        assertEquals(client, value)
    }

    @Test
    fun `should send transaction message to Kafka when API is called`() {
        val transaction = Transaction(
            bank = "bank",
            clientId = 8L,
            orderType = TransactionType.OUTCOME,
            quantity = 1,
            price = 0.8,
            createdAt = LocalDateTime.of(2015, 5, 31, 8, 20)
        )

        val response =
            restTemplate.exchange("/transactions", POST, HttpEntity<Transaction>(transaction), Unit::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        val records = kafkaConsumer.poll(Duration.of(5, ChronoUnit.SECONDS))

        val kafkaMessage = records.records(TRANSACTION_TOPIC).firstOrNull()
        assertNotNull(kafkaMessage)

        val key = kafkaMessage.key()
        assertEquals(transaction.clientId, key)

        val value = kafkaMessage.value().let { objectMapper.readValue(it, Transaction::class.java) }
        assertEquals(transaction, value)
    }
}
