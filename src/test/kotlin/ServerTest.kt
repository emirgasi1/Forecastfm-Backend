package com.example

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.*

class ServerTest {

    @Test
    fun `test health endpoint`() = testApplication {
        application {
            configureResources()
            configureSerialization()
            configureRouting()
        }

        val response = client.get("/api/health")

        assertEquals(HttpStatusCode.OK, response.status)
    }
}