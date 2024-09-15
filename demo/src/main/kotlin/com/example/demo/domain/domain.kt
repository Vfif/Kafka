package com.example.demo.domain

import java.time.LocalDateTime

data class Client(
    val id: Long,
    val email: String,
    val firstName: String,
    val lastName: String,
)

data class Transaction(
    val bank: String,
    val clientId: Long,
    val orderType: TransactionType,
    val quantity: Int,
    val price: Double,
    val createdAt: LocalDateTime
)

enum class TransactionType {
    INCOME,
    OUTCOME
}

class KafkaSendException(ex: Throwable) : Exception(ex)
