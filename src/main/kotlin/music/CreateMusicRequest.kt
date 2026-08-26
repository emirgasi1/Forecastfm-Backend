package com.example.music

import kotlinx.serialization.Serializable

@Serializable
data class CreateMusicRequest(
    val title: String,
    val artist: String,
    val duration: String,
    val albumImageUrl: String?
)