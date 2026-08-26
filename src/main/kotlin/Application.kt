package com.example

import com.example.database.DatabaseFactory
import com.example.user.UserRepository
import io.ktor.server.application.Application

fun Application.rootModule() {

    DatabaseFactory.init()
    configureResources()
    configureSerialization()
    configureRouting()
}
fun Application.databaseModule() {
    DatabaseFactory.init()

    rootModule()
}
