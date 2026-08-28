package com.example.music.musichistory

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class MusicHistoryEntry(
    val id: String,
    val playlistId: String,
    val title: String,
    val weather: String,
    val temperature: String,
    val location: String,
    val playedAt: String
)