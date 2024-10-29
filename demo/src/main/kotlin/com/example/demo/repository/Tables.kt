package com.example.demo.repository

import com.example.demo.domain.TransactionType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime

object Clients : Table("client") {
    val id = long("id")
    val email = varchar("email", 50)
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    override val primaryKey = PrimaryKey(id, name = "clients_pk_id")
}

object Transactions : Table("transactions") {
    private val id = long("id").autoIncrement()
    val clientId = long("client_id").uniqueIndex().references(Clients.id)
    val bank = varchar("bank", 50)
    val orderType = enumeration<TransactionType>("order_id")
    val quantity = integer("quantity")
    val price = double("price")
    val cost = double("cost")
    val createAt = datetime("create_at")
    override val primaryKey = PrimaryKey(id, name = "transactions_pk_id")
}