package com.example.playlist

import kotlinx.serialization.Serializable

@Serializable
data class Playlist(
    val id: String,
    val title: String,
    val genre: String,
    val mood: String,
    val albumImageUrl: String?,
    val weather: String,
    val temperature: String,
    val location: String,
    val likes: Int,
    val spotifyUrl: String?,
    val youtubeUrl: String?
)