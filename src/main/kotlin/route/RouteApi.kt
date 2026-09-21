package com.example.route

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

class RouteApi(
    private val client: HttpClient
) {
    private val apiKey: String = System.getenv("ORS_API_KEY") ?: ""

    suspend fun getRoute(request: RouteRequest): RouteResponse {
        if (apiKey.isBlank()) {
            throw Exception("ORS API key not set")
        }

        val profile = when (request.mode.lowercase()) {
            "driving" -> "driving-car"
            else -> "foot-walking"
        }

        val url = "https://api.openrouteservice.org/v2/directions/$profile"

        val response = client.post(url) {
            header(HttpHeaders.Authorization, apiKey)
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                OrsRequest(
                    coordinates = listOf(
                        listOf(request.fromLng, request.fromLat),
                        listOf(request.toLng, request.toLat)
                    )
                )
            )
        }

        if (response.status != HttpStatusCode.OK) {
            throw Exception("ORS error: ${response.status}")
        }

        val body: OrsResponse = response.body()

        if (body.routes.isEmpty()) {
            throw Exception("ORS returned no routes")
        }

        val route = body.routes.first()

        return RouteResponse(
            geometry = route.geometry,
            distanceMeters = route.summary.distance,
            durationSeconds = route.summary.duration
        )
    }
}

@Serializable
private data class OrsRequest(
    val coordinates: List<List<Double>>
)

@Serializable
private data class OrsResponse(
    val routes: List<OrsRoute> = emptyList()
)

@Serializable
private data class OrsRoute(
    val geometry: String = "",
    val summary: OrsSummary = OrsSummary()
)

@Serializable
private data class OrsSummary(
    val distance: Double = 0.0,
    val duration: Double = 0.0
)