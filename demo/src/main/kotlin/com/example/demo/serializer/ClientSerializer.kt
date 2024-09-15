package com.example.demo.serializer

import com.example.demo.domain.Client
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serializer


class ClientSerializer : Serializer<Client> {
    private val objectMapper = ObjectMapper()
    override fun serialize(topic: String, data: Client): ByteArray {
        return try {
            objectMapper.writeValueAsBytes(data)
        } catch (e: JsonProcessingException) {
            throw RuntimeException("Error serializing Client object", e)
        }
    }
}
