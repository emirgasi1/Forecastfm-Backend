package com.example.post

import kotlinx.serialization.Serializable

@Serializable
data class CreatePostRequest(
    val userId: String,
    val caption: String?,
    val imageUrl: String?,
    val outfitId: String? = null
)