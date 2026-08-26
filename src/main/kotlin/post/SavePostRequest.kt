package com.example.post

import kotlinx.serialization.Serializable

@Serializable
data class SavePostRequest(
    val userId: String
)