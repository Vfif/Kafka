package com.example.demo.producers

import com.example.demo.config.KafkaConfig
import com.example.demo.domain.Client
import com.example.demo.domain.KafkaSendException
import com.example.demo.serializer.ClientSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import org.springframework.stereotype.Component
import java.util.*

@Component
class ClientProducer {
    val producer = KafkaProducer<Long, Client>(props)

    fun send(client: Client): Client {
        producer.send(ProducerRecord(KafkaConfig.CLIENT_TOPIC, client.id, client))
        { _, ex ->
            ex
                ?.let { throw KafkaSendException(it) }
                ?: println("Key = ${client.id} value = $client")
        }
        return client
    }

    companion object {
        val props = object : Properties() {
            init {
                this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = KafkaConfig.SERVER_CONFIG
                this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java
                this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = ClientSerializer::class.java
                this[ProducerConfig.ACKS_CONFIG] = KafkaConfig.ACKS_CONFIG
            }
        }
    }
}
