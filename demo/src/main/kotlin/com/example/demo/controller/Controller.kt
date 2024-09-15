package com.example.demo.controller

import com.example.demo.domain.Client
import com.example.demo.domain.Transaction
import com.example.demo.service.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller(private val service: Service) {
    @PostMapping("/clients")
    fun sendClient(@RequestBody client: Client): Client = service.send(client)

    @PostMapping("/transactions")
    fun sendTransaction(@RequestBody transaction: Transaction): Transaction = service.send(transaction)
}
