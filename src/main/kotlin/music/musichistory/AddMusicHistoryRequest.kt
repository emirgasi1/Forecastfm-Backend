package com.example.music.musichistory

import kotlinx.serialization.Serializable

@Serializable
data class AddMusicHistoryRequest(
    val userId: String,
    val playlistId: String
)