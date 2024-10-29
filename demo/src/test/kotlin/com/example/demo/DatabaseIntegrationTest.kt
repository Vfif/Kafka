package com.example.demo

import com.example.demo.domain.Client
import com.example.demo.domain.Transaction
import com.example.demo.domain.TransactionType
import com.example.demo.repository.ClientRepository
import com.example.demo.repository.TransactionRepository
import org.junit.jupiter.api.Assertions.assertEquals
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
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.LocalDateTime


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DatabaseIntegrationTest(
    @Autowired val restTemplate: TestRestTemplate,
    @Autowired val clientRepository: ClientRepository,
    @Autowired val transactionRepository: TransactionRepository,
) {
    companion object {
        private const val DB_NAME = "test"
        private const val USERNAME = "testUser"
        private const val PASSWORD = "testPassword"

        @Container
        @JvmStatic
        private val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.4"))

        @Container
        @JvmStatic
        private val postgreSQLContainer = PostgreSQLContainer("postgres:13")
            .withDatabaseName(DB_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD)

        @DynamicPropertySource
        @JvmStatic
        fun dbProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers") { kafkaContainer.bootstrapServers }
            registry.add("spring.datasource.url") { postgreSQLContainer.jdbcUrl + "/" + DB_NAME }
            registry.add("spring.datasource.username") { USERNAME }
            registry.add("spring.datasource.password") { PASSWORD }
        }
    }


    @Test
    fun `should send client message to Kafka, receive from kafka and save in db`() {
        val client = Client(
            id = 11L,
            email = "user@gmail.com",
            firstName = "User",
            lastName = "Admin"
        )

        val response =
            restTemplate.exchange("/clients", POST, HttpEntity<Client>(client), Unit::class.java)
        assertEquals(HttpStatus.OK, response.statusCode)

        val value = clientRepository.get(client.id)
        assertEquals(client, value)
    }

    @Test
    fun `should send transaction message to Kafka, receive from kafka and save in db`() {
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

        val value = transactionRepository.get(transaction.clientId)
        assertEquals(transaction, value)
    }
}
