package com.example.demo.repository

import com.example.demo.domain.Transaction
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository

@Repository
class TransactionRepository {
    fun save(transaction: Transaction) {
        transaction {
            Transactions.upsert {
                it[bank] = transaction.bank
                it[orderType] = transaction.orderType
                it[quantity] = transaction.quantity
                it[price] = transaction.price
                it[cost] = transaction.cost()
                it[clientId] = transaction.clientId
                it[createAt] = transaction.createdAt
            }
        }
    }

    fun get(clientId: Long): List<Transaction> {
        return transaction {
            Transactions.selectAll()
                .where { Transactions.clientId eq clientId }
                .map { it.toTransaction() }
        }
    }

    private fun ResultRow.toTransaction() =
        Transaction(
            clientId = this[Transactions.clientId],
            bank = this[Transactions.bank],
            orderType = this[Transactions.orderType],
            quantity = this[Transactions.quantity],
            price = this[Transactions.price],
            createdAt = this[Transactions.createAt]
        )
}

