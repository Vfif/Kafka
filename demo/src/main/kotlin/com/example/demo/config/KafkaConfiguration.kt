package com.example.demo.config

import com.example.demo.consumer.ClientService
import com.example.demo.consumer.ConsumerService
import com.example.demo.consumer.TransactionService
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class KafkaConfiguration {
    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Value("\${spring.kafka.key-serializer}")
    private lateinit var keySerializer: String

    @Value("\${spring.kafka.value-serializer}")
    private lateinit var valueSerializer: String

    @Value("\${spring.kafka.consumer.key-deserializer}")
    private lateinit var keyDeserializer: String

    @Value("\${spring.kafka.consumer.value-deserializer}")
    private lateinit var valueDeserializer: String

    @Value("\${spring.kafka.acks}")
    private lateinit var acks: String

    @Value("\${spring.kafka.consumer.group-id}")
    private lateinit var consumerGroupId: String

    @Value("\${spring.kafka.consumer.auto-offset-reset}")
    private lateinit var autoOffsetReset: String

    @Value("\${spring.kafka.topic.client}")
    private lateinit var clientTopic: String

    @Value("\${spring.kafka.topic.transaction}")
    private lateinit var transactionTopic: String

    @Bean
    fun kafkaProducerProperties(): Properties {
        return Properties().apply {
            this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
            this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = keySerializer
            this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = valueSerializer
            this[ProducerConfig.ACKS_CONFIG] = acks
        }
    }

    @Bean
    fun kafkaConsumerProperties(): Properties {
        return Properties().apply {
            this[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
            this[ConsumerConfig.GROUP_ID_CONFIG] = consumerGroupId
            this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = keyDeserializer
            this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = valueDeserializer
            this[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = autoOffsetReset
        }
    }

    @Bean
    fun kafkaProducer(): KafkaProducer<Long, String> {
        return KafkaProducer(kafkaProducerProperties())
    }

    @Bean
    fun clientKafkaConsumer(): KafkaConsumer<Long, String> {
        return KafkaConsumer<Long, String>(kafkaConsumerProperties())
            .apply { subscribe(listOf(clientTopic)) }
    }

    @Bean
    fun transactionKafkaConsumer(): KafkaConsumer<Long, String> {
        return KafkaConsumer<Long, String>(kafkaConsumerProperties())
            .apply { subscribe(listOf(transactionTopic)) }
    }

    @Bean
    fun clientConsumerService(service: ClientService): Thread {
        return Thread(
            ConsumerService(clientKafkaConsumer(), service)
        ).apply { start() }
    }

    @Bean
    fun transactionConsumerService(service: TransactionService): Thread {
        return Thread(
            ConsumerService(transactionKafkaConsumer(), service)
        ).apply { start() }
    }
}