package com.example.weather

import java.util.concurrent.ConcurrentHashMap

object WeatherCache {
    private data class CachedEntry(
        val data: OpenMeteoResponse,
        val timestamp: Long
    )

    private val cache = ConcurrentHashMap<String, CachedEntry>()
    private const val TTL_MS = 30 * 60 * 1000L

    fun get(latitude: Double, longitude: Double): OpenMeteoResponse? {
        val key = "$latitude,$longitude"
        val entry = cache[key] ?: return null
        if (System.currentTimeMillis() - entry.timestamp > TTL_MS) {
            cache.remove(key)
            return null
        }
        return entry.data
    }

    fun put(latitude: Double, longitude: Double, data: OpenMeteoResponse) {
        cache["$latitude,$longitude"] = CachedEntry(data, System.currentTimeMillis())
    }
}