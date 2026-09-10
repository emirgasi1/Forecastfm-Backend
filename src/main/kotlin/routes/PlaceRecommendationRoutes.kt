package com.example.routes

import com.example.placerecommendation.CreatePlaceRecommendationRequest
import com.example.placerecommendation.PlaceRecommendationRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.placeRecommendationRoutes() {
    val repository = PlaceRecommendationRepository()

    post("/api/place-recommendations") {
        val request = call.receive<CreatePlaceRecommendationRequest>()
        val recommendation = repository.createPlaceRecommendation(
            name = request.name,
            category = request.category,
            location = request.location,
            description = request.description,
            suitableFor = request.suitableFor,
            weatherCondition = request.weatherCondition,
            ageGroup = request.ageGroup,
            rating = request.rating,
            imageUrl = request.imageUrl
        )
        call.respond(HttpStatusCode.Created, recommendation)
    }

    get("/api/place-recommendations") {
        val category = call.request.queryParameters["category"]
        val suitableFor = call.request.queryParameters["suitableFor"]
        val weatherCondition = call.request.queryParameters["weatherCondition"]
        val ageGroup = call.request.queryParameters["ageGroup"]

        val recommendations = repository.getRecommendationsByFilters(
            category = category,
            suitableFor = suitableFor,
            weatherCondition = weatherCondition,
            ageGroup = ageGroup
        )
        call.respond(recommendations)
    }

    get("/api/place-recommendations/{id}") {
        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ID")

        val recommendation = repository.getRecommendationById(id)
        if (recommendation == null) {
            call.respond(HttpStatusCode.NotFound, "Recommendation not found")
        } else {
            call.respond(recommendation)
        }
    }
}