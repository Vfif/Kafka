package com.example.demo.controller

import com.example.demo.config.KafkaConfig
import com.example.demo.domain.Client
import com.example.demo.domain.Transaction
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(
    private val objectMapper: ObjectMapper,
    private val kafkaProducer: KafkaProducer<Long, String>
) {
    @PostMapping("/clients")
    fun sendClient(@RequestBody client: Client) {
        val message = ProducerRecord(
            KafkaConfig.CLIENT_TOPIC,
            client.id,
            objectMapper.writeValueAsString(client)
        )
        kafkaProducer.send(message)
    }

    @PostMapping("/transactions")
    fun sendTransaction(@RequestBody transaction: Transaction) {
        val message = ProducerRecord(
            KafkaConfig.TRANSACTION_TOPIC,
            transaction.clientId,
            objectMapper.writeValueAsString(transaction)
        )
        kafkaProducer.send(message)
    }
}
