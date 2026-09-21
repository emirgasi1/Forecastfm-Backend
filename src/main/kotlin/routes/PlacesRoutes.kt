package com.example.routes

import com.example.place.PlacesRepository
import com.example.place.SavedPlaceRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.placesRoutes() {
    val repository = PlacesRepository()

    get("/api/places/venue/{venueId}") {
        val venueId = call.parameters["venueId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing venue ID")

        val places = repository.getPlacesByVenue(venueId)
        call.respond(places)
    }

    get("/api/places/search") {
        val query = call.request.queryParameters["q"]
            ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Missing query parameter 'q'"
            )

        try {
            val places = repository.searchPlaces(query)
            call.respond(places)
        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                e.message ?: "Search failed"
            )
        }
    }
    get("/api/places/count") {
        val venueId = call.request.queryParameters["venueId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing venueId")

        val count = PlacesRepository().countPlacesByVenue(venueId)
        call.respond(mapOf("count" to count))
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
    get("/api/places") {
        val places = repository.getAllPlaces()
        call.respond(places)
    }
    post("/api/places/{placeId}/save") {
        val placeId = call.parameters["placeId"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing place ID")

        val userId = call.request.headers["User-Id"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        SavedPlaceRepository().savePlace(userId, placeId)
        call.respond(HttpStatusCode.Created)
    }

    delete("/api/places/{placeId}/save") {
        val placeId = call.parameters["placeId"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing place ID")

        val userId = call.request.headers["User-Id"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        SavedPlaceRepository().unsavePlace(userId, placeId)
        call.respond(HttpStatusCode.OK)
    }

    get("/api/places/{placeId}/save") {
        val placeId = call.parameters["placeId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing place ID")

        val userId = call.request.headers["User-Id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        val isSaved = SavedPlaceRepository().isPlaceSaved(userId, placeId)
        call.respond(mapOf("saved" to isSaved))
    }

    get("/api/places/saved") {
        val userId = call.request.headers["User-Id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        val saved = SavedPlaceRepository().getSavedPlaces(userId)
        call.respond(saved)
    }
}