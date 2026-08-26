package com.example.comment


import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String,
    val userId: String,
    val postId: String,
    val text: String,
    val createdAt: String,
    val likes: Int
)