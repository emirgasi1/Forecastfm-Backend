package com.example.model

import kotlinx.serialization.Serializable

@Serializable
data class HealthResponse(
    val status: String
)