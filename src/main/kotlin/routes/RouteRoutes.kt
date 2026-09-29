package com.example.routes

import com.example.route.RouteApi
import com.example.route.RouteRequest
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.routeRoutes(
    httpClient: HttpClient,
    apiKey: String = System.getenv("ORS_API_KEY") ?: ""
) {
    val routeApi = RouteApi(httpClient, apiKey)

    post("/api/route") {
        try {
            val request = call.receive<RouteRequest>()
            val response = routeApi.getRoute(request)
            call.respond(response)
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Route calculation failed"
            )
        }
    }
}