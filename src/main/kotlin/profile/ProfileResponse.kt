package com.example.profile

import com.example.playlist.Playlist
import com.example.playlist.PlaylistResponse
import com.example.post.Post
import kotlinx.serialization.Serializable

@Serializable
data class ProfileResponse(
    val id: String,
    val username: String,
    val bio: String?,
    val profileImageUrl: String?,
    val favoriteLocation: String?,
    val likes: Int,
    val saved: Int,
    val posts: List<Post>,
    val favoritePlaylists: List<PlaylistResponse>
)