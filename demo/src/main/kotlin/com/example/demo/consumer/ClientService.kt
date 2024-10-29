package com.example.demo.consumer

import com.example.demo.domain.Client
import com.example.demo.repository.ClientRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.stereotype.Service

@Service
class ClientService (
    private val clientRepository: ClientRepository,
    private val objectMapper: ObjectMapper,
): ProcessService {

    override fun process(record: ConsumerRecord<Long, String>) {
        val client = objectMapper.readValue(record.value(), Client::class.java)
        clientRepository.save(client)
    }
}