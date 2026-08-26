package com.example.playlist

import com.example.music.Music
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistResponse(
    val id: String,
    val title: String,
    val genre: String,
    val mood: String,
    val albumImageUrl: String?,
    val weather: String,
    val temperature: String,
    val location: String,
    val songs: List<Music>,
    val likes: Int
)