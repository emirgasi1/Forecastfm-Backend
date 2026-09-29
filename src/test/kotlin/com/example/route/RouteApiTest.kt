package com.example.route

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class RouteApiTest {

    private fun createMockClient(
        responseBody: String,
        status: HttpStatusCode = HttpStatusCode.OK
    ): HttpClient {
        val mockEngine = MockEngine { _ ->
            respond(
                content = responseBody,
                status = status,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Test
    fun `getRoute returns mapped RouteResponse on successful ORS call`() = runBlocking {
        val mockResponseBody = """
            {
              "routes": [
                {
                  "geometry": "encoded_polyline_string_xyz",
                  "summary": {
                    "distance": 1250.5,
                    "duration": 900.0
                  }
                }
              ]
            }
        """.trimIndent()

        val client = createMockClient(mockResponseBody)
        val api = RouteApi(client = client, apiKey = "test_key_123")

        val request = RouteRequest(
            fromLat = 40.7128,
            fromLng = -74.0060,
            toLat = 40.7306,
            toLng = -73.9352,
            mode = "walking"
        )

        val result = api.getRoute(request)

        assertEquals("encoded_polyline_string_xyz", result.geometry)
        assertEquals(1250.5, result.distanceMeters)
        assertEquals(900.0, result.durationSeconds)
    }

    @Test
    fun `getRoute maps driving mode to driving-car profile`() = runBlocking {
        var requestedUrl = ""
        val mockEngine = MockEngine { request ->
            requestedUrl = request.url.toString()
            respond(
                content = """{"routes":[{"geometry":"geo","summary":{"distance":10.0,"duration":5.0}}]}""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }
        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) { json() }
        }

        val api = RouteApi(client = client, apiKey = "test_key_123")
        val request = RouteRequest(
            fromLat = 40.0, fromLng = -70.0,
            toLat = 40.1, toLng = -70.1,
            mode = "driving"
        )

        api.getRoute(request)

        assertEquals("https://api.openrouteservice.org/v2/directions/driving-car", requestedUrl)
    }

    @Test
    fun `getRoute throws exception when ORS returns HTTP error`() {
        val client = createMockClient("""{"error":"Unauthorized"}""", HttpStatusCode.Unauthorized)
        val api = RouteApi(client = client, apiKey = "test_key_123")

        val request = RouteRequest(40.0, -70.0, 40.1, -70.1)

        val exception = assertThrows<Exception> {
            runBlocking { api.getRoute(request) }
        }
        assertEquals("ORS error: 401 Unauthorized", exception.message)
    }

    @Test
    fun `getRoute throws exception when ORS returns empty routes array`() {
        val client = createMockClient("""{"routes":[]}""")
        val api = RouteApi(client = client, apiKey = "test_key_123")

        val request = RouteRequest(40.0, -70.0, 40.1, -70.1)

        val exception = assertThrows<Exception> {
            runBlocking { api.getRoute(request) }
        }
        assertEquals("ORS returned no routes", exception.message)
    }

    @Test
    fun `getRoute throws exception when API key is blank`() {
        val client = createMockClient("""{}""")
        val api = RouteApi(client = client, apiKey = "")

        val request = RouteRequest(40.0, -70.0, 40.1, -70.1)

        val exception = assertThrows<Exception> {
            runBlocking { api.getRoute(request) }
        }
        assertEquals("ORS API key not set", exception.message)
    }
}