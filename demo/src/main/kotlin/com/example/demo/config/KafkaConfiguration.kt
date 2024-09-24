package com.example.demo.config

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

    @Value("\${spring.kafka.acks}")
    private lateinit var acks: String

    @Bean
    fun kafkaProperties(): Properties {
        return Properties().apply {
            this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
            this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = keySerializer
            this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = valueSerializer
            this[ProducerConfig.ACKS_CONFIG] = acks
        }
    }

    @Bean
    fun kafkaProducer(): KafkaProducer<Long, String> {
        return KafkaProducer(kafkaProperties())
    }
}