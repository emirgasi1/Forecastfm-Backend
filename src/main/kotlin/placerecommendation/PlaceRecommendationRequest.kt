package com.example.placerecommendation

import kotlinx.serialization.Serializable

@Serializable
data class CreatePlaceRecommendationRequest(
    val name: String,
    val category: String,
    val location: String,
    val placeId: String,
    val description: String,
    val suitableFor: List<String>,
    val weatherCondition: String,
    val ageGroup: List<String>,
    val rating: Double,
    val imageUrl: String? = null
)