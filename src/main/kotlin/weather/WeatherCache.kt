package com.example.weather

import java.util.concurrent.ConcurrentHashMap

object WeatherCache {

    private data class CachedEntry(
        val data: OpenMeteoResponse,
        val timestamp: Long
    )

    private val cache = ConcurrentHashMap<String, CachedEntry>()

    private const val FRESH_TTL_MS = 6 * 60 * 60 * 1000L
    private const val STALE_TTL_MS = 48 * 60 * 60 * 1000L

    fun getFresh(latitude: Double, longitude: Double): OpenMeteoResponse? {
        val key = "$latitude,$longitude"
        val entry = cache[key] ?: return null
        val age = System.currentTimeMillis() - entry.timestamp
        return if (age <= FRESH_TTL_MS) entry.data else null
    }

    fun getStale(latitude: Double, longitude: Double): OpenMeteoResponse? {
        val key = "$latitude,$longitude"
        val entry = cache[key] ?: return null
        val age = System.currentTimeMillis() - entry.timestamp
        return if (age <= STALE_TTL_MS) entry.data else null
    }

    fun put(latitude: Double, longitude: Double, data: OpenMeteoResponse) {
        cache["$latitude,$longitude"] = CachedEntry(data, System.currentTimeMillis())
    }
}