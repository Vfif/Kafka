package com.example.demo.repository

import com.example.demo.domain.Client
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import org.springframework.stereotype.Repository

@Repository
class ClientRepository {
    fun save(client: Client) {
        transaction {
            Clients.upsert {
                it[id] = client.id
                it[email] = client.email
                it[firstName] = client.firstName
                it[lastName] = client.lastName
            }
        }
    }

    fun get(id: Long): Client? {
        return transaction {
            Clients.selectAll()
                .where { Clients.id eq id }
                .map { it.toClient() }
                .firstOrNull()
        }
    }

    private fun ResultRow.toClient() =
        Client(
            id = this[Clients.id],
            email = this[Clients.email],
            firstName = this[Clients.firstName],
            lastName = this[Clients.lastName],
        )
}


