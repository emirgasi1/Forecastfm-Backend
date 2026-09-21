package com.example.places

import kotlinx.serialization.Serializable

@Serializable
internal data class OverpassResponse(
    val elements: List<OverpassElement> = emptyList()
)

@Serializable
internal data class OverpassElement(
    val type: String = "",
    val id: Long = 0,
    val lat: Double? = null,
    val lon: Double? = null,
    val center: OverpassCenter? = null,
    val tags: Map<String, String> = emptyMap()
)

@Serializable
internal data class OverpassCenter(
    val lat: Double = 0.0,
    val lon: Double = 0.0
)