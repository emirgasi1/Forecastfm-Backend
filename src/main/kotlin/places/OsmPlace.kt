package com.example.places

data class OsmPlace(
    val osmId: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val address: String?,
    val phone: String?,
    val website: String?
)