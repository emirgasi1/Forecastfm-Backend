package com.example.music

import kotlinx.serialization.Serializable

@Serializable
data class Music(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val albumImageUrl: String?
)