package com.example.user

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val username: String,
    val bio: String?,
    val profileImageUrl: String?,
    val favoriteLocation: String?
)