package com.example.demo.producers

import com.example.demo.config.KafkaConfig
import com.example.demo.domain.KafkaSendException
import com.example.demo.domain.Transaction
import com.example.demo.serializer.TransactionSerializer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import org.springframework.stereotype.Component
import java.util.*

@Component
class TransactionProducer {
    val producer = KafkaProducer<Long, Transaction>(props)

    fun send(transaction: Transaction): Transaction {
        producer.send(ProducerRecord(KafkaConfig.TRANSACTION_TOPIC, transaction.clientId, transaction))
        { _, ex ->
            ex
                ?.let { throw KafkaSendException(it) }
                ?: println("Key = ${transaction.clientId} value = $transaction")
        }
        return transaction
    }

    companion object {
        val props = object : Properties() {
            init {
                this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = KafkaConfig.SERVER_CONFIG
                this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java
                this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = TransactionSerializer::class.java
                this[ProducerConfig.ACKS_CONFIG] = KafkaConfig.ACKS_CONFIG
            }
        }
    }
}
