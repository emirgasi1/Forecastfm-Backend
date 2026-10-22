package com.example.database.seed

import com.example.database.table.Venues
import com.example.venue.Venue
import com.example.venue.VenuesRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
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

        val stream = javaClass.classLoader
            .getResourceAsStream("sarajevo_places.geojson")
            ?: run {
                println("[Seeder] sarajevo_places.geojson not found in resources")
                return
            }

        val root = stream.bufferedReader().use { it.readText() }
        val parsed = json.parseToJsonElement(root).jsonObject
        val features = parsed["features"]?.jsonArray ?: JsonArray(emptyList())

        val venues = mutableListOf<Venue>()
        val seenIds = mutableSetOf<String>()

        for (feature in features) {
            val f = feature.jsonObject
            val props = f["properties"]?.jsonObject ?: continue
            val name = props["name"]?.jsonPrimitive?.contentOrNull ?: continue

            val category = props["amenity"]?.jsonPrimitive?.contentOrNull
                ?: props["tourism"]?.jsonPrimitive?.contentOrNull
                ?: props["highway"]?.jsonPrimitive?.contentOrNull
                ?: continue

            val rawId = f["id"]?.jsonPrimitive?.contentOrNull
                ?: props["@id"]?.jsonPrimitive?.contentOrNull
                ?: continue

            val id = rawId.replace("/", "-")
            if (!seenIds.add(id)) continue

            val coords = extractCoordinates(f) ?: continue

            venues += Venue(
                id = id,
                name = name,
                category = category,
                locationId = null,
                address = null,
                latitude = coords.first,
                longitude = coords.second,
                description = null,
                imageUrl = null,
                createdAt = Instant.now().toString()
            )
        }

        println("[Seeder] Parsed ${features.size} features → ${venues.size} unique venues")
        if (venues.isNotEmpty()) {
            VenuesRepository().bulkInsert(venues)
            println("[Seeder] Inserted ${venues.size} venues")
        }
    }

    /**
     * GeoJSON can come in two shapes here:
     *  - Point:  geometry.coordinates = [lon, lat]
     *  - Polygon: geometry.center     = { lat, lon }   (Overpass `out center;` for ways)
     */
    private fun extractCoordinates(feature: JsonObject): Pair<Double, Double>? {
        val geometry = feature["geometry"]?.jsonObject ?: return null

        // Prefer `center` for ways (Overpass `out center;`)
        val center = geometry["center"]?.jsonObject
        if (center != null) {
            val lat = center["lat"]?.jsonPrimitive?.doubleOrNull
            val lon = center["lon"]?.jsonPrimitive?.doubleOrNull
            if (lat != null && lon != null) return lat to lon
        }

        // Fall back to Point coordinates: [lon, lat]
        val type = geometry["type"]?.jsonPrimitive?.contentOrNull
        val coords = geometry["coordinates"]?.jsonArray
        if (type == "Point" && coords != null && coords.size >= 2) {
            val lon = coords[0].jsonPrimitive.doubleOrNull
            val lat = coords[1].jsonPrimitive.doubleOrNull
            if (lat != null && lon != null) return lat to lon
        }

        // Fallback: first coordinate of a polygon
        if (coords != null && coords.isNotEmpty()) {
            val first = coords[0]
            if (first is JsonArray && first.size >= 2) {
                val lon = first[0].jsonPrimitive.doubleOrNull
                val lat = first[1].jsonPrimitive.doubleOrNull
                if (lat != null && lon != null) return lat to lon
            }
        }

        return null
    }
}