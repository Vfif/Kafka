package com.example.demo.config

import com.example.demo.repository.Clients
import com.example.demo.repository.Transactions
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import javax.sql.DataSource

@Configuration
class PostgresDatabaseConfig {
    @Value("\${spring.datasource.driver}")
    private lateinit var driver: String
    @Value("\${spring.datasource.url}")
    private lateinit var url: String
    @Value("\${spring.datasource.username}")
    private lateinit var username: String
    @Value("\${spring.datasource.password}")
    private lateinit var password: String


    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(driver)
        dataSource.url = url
        dataSource.username = username
        dataSource.password = password
        return dataSource
    }


    @Bean
    fun database(): Database {
        return Database.connect(dataSource()).apply {
            transaction {
                SchemaUtils.create(Clients, Transactions)
            }
        }
    }
}