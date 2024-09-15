package com.example.demo.service

import com.example.demo.domain.Client
import com.example.demo.domain.Transaction
import com.example.demo.producers.ClientProducer
import com.example.demo.producers.TransactionProducer
import org.springframework.stereotype.Service


@Service
class Service(
    private val clientProducer: ClientProducer,
    private val transactionProducer: TransactionProducer
) {
    fun send(client: Client) = clientProducer.send(client)
    fun send(transaction: Transaction) = transactionProducer.send(transaction)
}
