package com.example

import com.example.configure.createHttpClient
import io.ktor.client.HttpClient
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

            val httpClient = createHttpClient()

            configureRouting(httpClient)
        }

        val response = client.get("/api/health")

        assertEquals(HttpStatusCode.OK, response.status)
    }
}