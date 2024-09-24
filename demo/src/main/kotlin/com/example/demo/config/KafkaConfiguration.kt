package com.example.demo.config

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class KafkaConfiguration {

    @Value("\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapServers: String

    @Bean
    fun kafkaProperties(): Properties {
        return Properties().apply {
            this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
            this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = LongSerializer::class.java
            this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
            this[ProducerConfig.ACKS_CONFIG] = KafkaConfig.ACKS_CONFIG
        }
    }

    @Bean
    fun kafkaProducer(): KafkaProducer<Long, String> {
        return KafkaProducer(kafkaProperties())
    }
}