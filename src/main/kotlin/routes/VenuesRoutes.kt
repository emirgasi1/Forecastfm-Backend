package com.example.routes

import com.example.venue.VenuesRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.venuesRoutes() {
    val repo = VenuesRepository()

    get("/api/venues") {
        call.respond(repo.getAllVenues())
    }

    get("/api/venues/{id}") {
        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing id")
        repo.getVenueById(id)?.let { call.respond(it) }
            ?: call.respond(HttpStatusCode.NotFound, "Venue not found")
    }

    get("/api/venues/location/{locationId}") {
        val locId = call.parameters["locationId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing locationId")
        call.respond(repo.getVenuesByLocation(locId))
    }

    get("/api/venues/search") {
        val q = call.request.queryParameters["q"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing q")
        call.respond(repo.searchVenues(q))
    }
}