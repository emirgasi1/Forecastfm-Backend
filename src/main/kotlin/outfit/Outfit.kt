package com.example.outfit

import kotlinx.serialization.Serializable

@Serializable
data class Outfit(
    val id: String,
    val userId: String,
    val imageUrl: String,
    val title: String,
    val weatherCondition: String,
    val season: String,
    val likes: Int = 0,
    val storeName: String? = null,
    val storeAddress: String? = null,
    val price: String? = null,
    val createdAt: String
)

@Serializable
data class CreateOutfitRequest(
    val userId: String,
    val imageUrl: String,
    val title: String,
    val weatherCondition: String,
    val season: String,
    val storeName: String? = null,
    val storeAddress: String? = null,
    val price: String? = null
)