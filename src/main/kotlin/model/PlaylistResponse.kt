package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistResponse(
    val id: String,
    val title: String,
    val genre: String,
    val mood: String,
    val weather: String,
    val temperature: String,
    val location: String,
    val likes: Int
)