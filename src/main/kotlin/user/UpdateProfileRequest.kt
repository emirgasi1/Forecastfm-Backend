package com.example.user

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val username: String,
    val bio: String? = null,
    val favoriteLocation: String? = null
)