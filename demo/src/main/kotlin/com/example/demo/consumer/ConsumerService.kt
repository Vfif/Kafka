package com.example.demo.consumer

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.Duration

class ConsumerService (
    private val kafkaConsumer: KafkaConsumer<Long, String>,
    private val service: ProcessService,
): Runnable {
    override fun run(){
        while (true) {
            val records = kafkaConsumer.poll(Duration.ofSeconds(5))
            for (record in records) {
                service.process(record)
            }
            kafkaConsumer.commitSync()
        }
    }
}