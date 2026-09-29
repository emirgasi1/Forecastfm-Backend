package com.example.routes

import com.example.route.RouteApi
import com.example.route.RouteRequest
import com.example.route.RouteResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RouteRoutesTest {

    private fun createMockOrsClient(
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
            install(ClientContentNegotiation) { json() }
        }
    }

    @Test
    fun `POST api route returns 200 OK and RouteResponse on success`() = testApplication {
        val mockResponseBody = """
            {
              "routes": [
                {
                  "geometry": "sample_geometry_data",
                  "summary": {
                    "distance": 5000.0,
                    "duration": 600.0
                  }
                }
              ]
            }
        """.trimIndent()

        val mockOrsClient = createMockOrsClient(mockResponseBody)

        application {
            install(ServerContentNegotiation) { json() }
            routing {
                // Pass test API key explicitly
                routeRoutes(mockOrsClient, apiKey = "test_key_123")
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val request = RouteRequest(
            fromLat = 51.5074,
            fromLng = -0.1278,
            toLat = 51.5010,
            toLng = -0.1416,
            mode = "driving"
        )

        val response = client.post("/api/route") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val routeResponse: RouteResponse = response.body()
        assertEquals("sample_geometry_data", routeResponse.geometry)
        assertEquals(5000.0, routeResponse.distanceMeters)
        assertEquals(600.0, routeResponse.durationSeconds)
    }

    @Test
    fun `POST api route returns 500 InternalServerError when ORS API key is missing`() = testApplication {
        val mockOrsClient = createMockOrsClient("{}")

        application {
            install(ServerContentNegotiation) { json() }
            routing {
                routeRoutes(mockOrsClient, apiKey = "")
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val request = RouteRequest(
            fromLat = 51.5074,
            fromLng = -0.1278,
            toLat = 51.5010,
            toLng = -0.1416,
            mode = "walking"
        )

        val response = client.post("/api/route") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
    }

    @Test
    fun `POST api route returns 500 InternalServerError when ORS returns an error status`() = testApplication {
        val mockOrsClient = createMockOrsClient("""{"error":"Forbidden"}""", HttpStatusCode.Forbidden)

        application {
            install(ServerContentNegotiation) { json() }
            routing {
                routeRoutes(mockOrsClient, apiKey = "test_key_123")
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val request = RouteRequest(
            fromLat = 51.5074,
            fromLng = -0.1278,
            toLat = 51.5010,
            toLng = -0.1416,
            mode = "walking"
        )

        val response = client.post("/api/route") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
    }
}