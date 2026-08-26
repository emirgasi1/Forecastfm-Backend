package com.example

import io.ktor.server.engine.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    embeddedServer(
        factory = io.ktor.server.netty.Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::databaseModule
    ).start(wait = true)
}
