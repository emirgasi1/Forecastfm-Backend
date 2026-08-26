package com.example.playlist

import kotlinx.serialization.Serializable

@Serializable
data class AddSongToPlaylistRequest(
    val musicId: String
)