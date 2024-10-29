package com.example.demo.consumer

import org.apache.kafka.clients.consumer.ConsumerRecord

interface ProcessService {
    fun process(record: ConsumerRecord<Long, String>)
}