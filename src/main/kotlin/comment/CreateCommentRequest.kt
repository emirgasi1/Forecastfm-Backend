package com.example.comment

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequest(
    val userId: String,
    val postId: String,
    val text: String
)