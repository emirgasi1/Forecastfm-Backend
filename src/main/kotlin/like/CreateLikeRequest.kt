package com.example.like

import kotlinx.serialization.Serializable

@Serializable
data class CreateLikeRequest(
    val userId: String,
)