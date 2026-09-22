package com.example.placerecommendation

import kotlinx.serialization.Serializable

@Serializable
data class PlaceRecommendationFromPlaceResponse(
    val id: String,
    val placeId: String,
    val name: String,
    val category: String,
    val location: String,
    val description: String,
    val suitableFor: List<String>,
    val weatherCondition: String,
    val ageGroup: List<String>,
    val rating: Double,
    val imageUrl: String? = null,
    val address: String? = null,
    val createdAt: String
)