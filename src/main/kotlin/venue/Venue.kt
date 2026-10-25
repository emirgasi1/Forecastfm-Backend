package com.example.venue

import kotlinx.serialization.Serializable

@Serializable
data class Venue(
    val id: String,
    val name: String,
    val category: String,
    val locationId: String?,
    val address: String?,
    val openingHours: String?,
    val latitude: Double,
    val longitude: Double,
    val description: String?,
    val imageUrl: String?,
    val createdAt: String
)