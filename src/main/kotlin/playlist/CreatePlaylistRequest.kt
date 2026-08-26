package com.example.playlist

import kotlinx.serialization.Serializable

@Serializable
data class CreatePlaylistRequest(
    val title: String,
    val genre: String,
    val mood: String,
    val albumImageUrl: String?,
    val weather: String,
    val temperature: String,
    val location: String,
    val spotifyUrl: String?,
    val youtubeUrl: String?
)