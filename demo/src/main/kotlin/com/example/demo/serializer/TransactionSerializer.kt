package com.example.demo.serializer

import com.example.demo.domain.Transaction
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serializer


class TransactionSerializer : Serializer<Transaction> {
    private val objectMapper = ObjectMapper()
    override fun serialize(topic: String, data: Transaction): ByteArray {
        return try {
            objectMapper.writeValueAsBytes(data)
        } catch (e: JsonProcessingException) {
            throw RuntimeException("Error serializing Transaction object", e)
        }
    }
}
