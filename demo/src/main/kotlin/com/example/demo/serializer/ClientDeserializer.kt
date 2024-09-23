package com.example.demo.serializer

import com.example.demo.domain.Client
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Deserializer

class ClientDeserializer : Deserializer<Client> {
    private val objectMapper = ObjectMapper()
    override fun configure(configs: Map<String?, *>?, isKey: Boolean) {}
    override fun deserialize(topic: String?, data: ByteArray?): Client? {
        return try {
            data?.let {
                objectMapper.readValue(String(data, charset("UTF-8")), Client::class.java)
            }
        } catch (e: Exception) {
            throw RuntimeException("Error when deserializing byte[] to MessageDto")
        }
    }

    override fun close() {}
}