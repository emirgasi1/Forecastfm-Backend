package com.example.place

import kotlinx.serialization.Serializable

@Serializable
data class Place(
    val id: String,
    val name: String,
    val category: String,
    val venueId: String?,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val imageUrl: String? = null,
    val createdAt: String
)