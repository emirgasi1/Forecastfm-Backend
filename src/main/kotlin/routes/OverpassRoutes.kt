package com.example.routes

import com.example.places.OverpassApi
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.overpassRoutes(httpClient: HttpClient) {
    val overpassApi = OverpassApi(httpClient)

    get("/api/osm/cafes") {
        try {
            val cafes = overpassApi.getCafesInSarajevo()
            call.respond(cafes)
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(
                HttpStatusCode.InternalServerError,
                mapOf("error" to (e.message ?: "Failed to fetch OSM cafes"))
            )
        }
    }
}