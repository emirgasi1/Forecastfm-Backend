package com.example.database.seed

import com.example.database.table.Venues
import com.example.venue.Venue
import com.example.venue.VenuesRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant

object DatabaseSeeder {

    private val json = Json { ignoreUnknownKeys = true }

    fun seedVenuesIfEmpty() {
        val existing = transaction { Venues.selectAll().count() }
        if (existing > 0L) {
            println("[Seeder] Venues already present: $existing rows — skipping")
            return
        }

        val locStream = javaClass.classLoader.getResourceAsStream("sarajevo_neighborhoods.geojson")
        if (locStream == null) {
            println("[Seeder] sarajevo_neighborhoods.geojson not found! Cannot assign locationIds.")
            return
        }
        val locRoot = locStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val locParsed = json.parseToJsonElement(locRoot).jsonObject
        val locFeatures = locParsed["features"]?.jsonArray ?: JsonArray(emptyList())

        val neighborhoods = locFeatures.mapNotNull { f ->
            val obj = f.jsonObject
            val props = obj["properties"]?.jsonObject ?: return@mapNotNull null
            val id = (obj["id"]?.jsonPrimitive?.contentOrNull ?: props["@id"]?.jsonPrimitive?.contentOrNull)?.replace("/", "-") ?: return@mapNotNull null
            val coords = extractCoordinates(obj) ?: return@mapNotNull null
            NeighborhoodPoint(id, coords.first, coords.second)
        }
        println("[Seeder] Loaded ${neighborhoods.size} neighborhoods for center-point mapping")

        // 2. Load Venues
        val stream = javaClass.classLoader.getResourceAsStream("sarajevo_places.geojson")
            ?: run {
                println("[Seeder] sarajevo_places.geojson not found in resources")
                return
            }

        val root = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val parsed = json.parseToJsonElement(root).jsonObject
        val features = parsed["features"]?.let { it as? JsonArray } ?: JsonArray(emptyList())

        val venues = mutableListOf<Venue>()
        val seenIds = mutableSetOf<String>()
        var filteredShortName = 0
        var filteredNoCoord = 0
        var filteredNoCategory = 0

        for (feature in features) {
            val f = feature.jsonObject
            val props = f["properties"]?.jsonObject ?: continue
            val name = props["name"]?.jsonPrimitive?.contentOrNull ?: continue

            val trimmed = name.trim()
            if (trimmed.length < 3 || trimmed.all { it.isDigit() }) {
                filteredShortName++
                continue
            }

            val category = props["amenity"]?.jsonPrimitive?.contentOrNull
                ?: props["tourism"]?.jsonPrimitive?.contentOrNull
                ?: props["highway"]?.jsonPrimitive?.contentOrNull
                ?: run { filteredNoCategory++; continue }

            val rawId = f["id"]?.jsonPrimitive?.contentOrNull
                ?: props["@id"]?.jsonPrimitive?.contentOrNull
                ?: continue

            val id = rawId.replace("/", "-")
            if (!seenIds.add(id)) continue

            val coords = extractCoordinates(f) ?: run { filteredNoCoord++; continue }

            // 3. Find the mathematically closest neighborhood to this venue
            var closestLocId: String? = null
            var minDistance = Double.MAX_VALUE

            for (n in neighborhoods) {
                val latDiff = coords.first - n.lat
                val lonDiff = coords.second - n.lon
                // Simple Euclidean distance is perfectly fine for a city scale
                val dist = (latDiff * latDiff) + (lonDiff * lonDiff)
                if (dist < minDistance) {
                    minDistance = dist
                    closestLocId = n.id
                }
            }

            venues += Venue(
                id = id,
                name = trimmed,
                category = category,
                locationId = closestLocId, // <-- REAL ID ASSIGNED HERE
                address = null,
                latitude = coords.first,
                longitude = coords.second,
                description = null,
                imageUrl = null,
                createdAt = Instant.now().toString()
            )
        }

        println("[Seeder] Parsed ${features.size} features → ${venues.size} unique venues " +
                "(dropped: shortName=$filteredShortName noCoord=$filteredNoCoord noCategory=$filteredNoCategory)")

        if (venues.isNotEmpty()) {
            VenuesRepository().bulkInsert(venues)
            println("[Seeder] Inserted ${venues.size} venues with assigned neighborhoods")
        }
    }

    private data class NeighborhoodPoint(val id: String, val lat: Double, val lon: Double)

    private fun extractCoordinates(feature: JsonObject): Pair<Double, Double>? {
        val geometry = feature["geometry"]?.jsonObject ?: return null

        val center = geometry["center"]?.jsonObject
        if (center != null) {
            val lat = (center["lat"] as? JsonPrimitive)?.doubleOrNull
            val lon = (center["lon"] as? JsonPrimitive)?.doubleOrNull
            if (lat != null && lon != null) return lat to lon
        }

        val coords = geometry["coordinates"] as? JsonArray ?: return null

        if (coords.size >= 2) {
            val lon = (coords[0] as? JsonPrimitive)?.doubleOrNull
            val lat = (coords[1] as? JsonPrimitive)?.doubleOrNull
            if (lat != null && lon != null) return lat to lon
        }

        var current: JsonArray? = coords
        var depth = 0
        while (current != null && depth < 5) {
            val first = current.firstOrNull()
            when (first) {
                is JsonPrimitive -> {
                    val lon = first.doubleOrNull
                    val lat = (current.getOrNull(1) as? JsonPrimitive)?.doubleOrNull
                    if (lat != null && lon != null) return lat to lon
                    return null
                }
                is JsonArray -> {
                    current = first
                    depth++
                }
                else -> return null
            }
        }

        return null
    }
}