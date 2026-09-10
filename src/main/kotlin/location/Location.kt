package com.example.location

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val id: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)