package com.example.demo.consumer

import com.example.demo.domain.Transaction
import com.example.demo.repository.TransactionRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.stereotype.Service

@Service
class TransactionService (
    private val transactionRepository: TransactionRepository,
    private val objectMapper: ObjectMapper,
): ProcessService {

    override fun process(record: ConsumerRecord<Long, String>) {
        val transaction = objectMapper.readValue(record.value(), Transaction::class.java)
        transactionRepository.save(transaction)
    }
}