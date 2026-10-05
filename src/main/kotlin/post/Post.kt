package com.example.post

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String,
    val userId: String,
    val caption: String?,
    val imageUrl: String?,
    val outfitId: String? = null,
    val outfitTitle: String? = null,
    val likes: Int = 0,
    val commentCount: Int = 0,
    val createdAt: String
)