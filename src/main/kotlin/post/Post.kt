package com.example.post

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class Post(
    val id: String,
    val userId: String,
    val caption: String?,
    val imageUrl: String?,
    val createdAt: String
)