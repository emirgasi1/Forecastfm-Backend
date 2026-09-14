package com.example.route

import kotlinx.serialization.Serializable

@Serializable
data class RouteRequest(
    val fromLat: Double,
    val fromLng: Double,
    val toLat: Double,
    val toLng: Double,
    val mode: String = "walking"
)

@Serializable
data class RouteResponse(
    val geometry: String,
    val distanceMeters: Double,
    val durationSeconds: Double
)