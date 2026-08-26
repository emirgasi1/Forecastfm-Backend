package com.example.user

data class User(
    val id: String,
    val username: String,
    val bio: String?,
    val profileImageUrl: String?,
    val favoriteLocation: String?
)