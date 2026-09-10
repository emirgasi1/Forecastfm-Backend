package com.example.routes

import com.example.place.PlacesRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.placesRoutes() {
    val repository = PlacesRepository()

    get("/api/places/venue/{venueId}") {
        val venueId = call.parameters["venueId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing venue ID")

        val places = repository.getPlacesByVenue(venueId)
        call.respond(places)
    }

    get("/api/places/{id}") {
        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing place ID")

        val place = repository.getPlaceById(id)
        if (place == null) {
            call.respond(HttpStatusCode.NotFound, "Place not found")
        } else {
            call.respond(place)
        }
    }
}