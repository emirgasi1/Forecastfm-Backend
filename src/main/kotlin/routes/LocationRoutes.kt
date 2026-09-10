package com.example.routes

import com.example.location.LocationRepository
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.response.respond

fun Route.locationRoutes() {

    val locationRepository= LocationRepository()

    get("/api/locations") {
        call.respond(locationRepository.getLocations())
    }
}