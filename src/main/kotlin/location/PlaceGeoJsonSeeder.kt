package com.example.location

import com.example.database.table.Locations
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

@Serializable
data class GeoJsonFeatureCollection(
    val features: List<GeoJsonFeature>
)

@Serializable
data class GeoJsonFeature(
    val id: String,
    val properties: Map<String, JsonElement>?,
    val geometry: GeoJsonGeometry?
)

@Serializable
data class GeoJsonGeometry(
    val type: String,
    val coordinates: JsonElement? = null,
    val center: GeoJsonCenter? = null
)

@Serializable
data class GeoJsonCenter(val lat: Double, val lon: Double)

object PlaceGeoJsonSeeder {

    private val NEIGHBORHOOD_CATEGORIES = setOf("suburb", "neighbourhood", "quarter")

    fun seed() {
        val existing = transaction { Locations.selectAll().count() }
        if (existing > 0L) {
            println("PlaceGeoJsonSeeder: Locations already present ($existing) — skipping")
            return
        }

        // ← reads sarajevo_neighborhoods.geojson (the small 123-feature file)
        val inputStream = PlaceGeoJsonSeeder::class.java.classLoader
            .getResourceAsStream("sarajevo_neighborhoods.geojson")

        if (inputStream == null) {
            println("PlaceGeoJsonSeeder: 'sarajevo_neighborhoods.geojson' NOT FOUND in resources!")
            return
        }

        println("PlaceGeoJsonSeeder: Starting...")
        // ← UTF-8 to fix mojibake
        val jsonString = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val json = Json { ignoreUnknownKeys = true }

        try {
            val featureCollection = json.decodeFromString<GeoJsonFeatureCollection>(jsonString)
            println("PlaceGeoJsonSeeder: ${featureCollection.features.size} features to process")

            var inserted = 0
            var skipped = 0
            var filteredOut = 0

            transaction {
                featureCollection.features.forEach { feature ->
                    val props = feature.properties
                    val name = props?.get("name")?.jsonPrimitive?.contentOrNull ?: run {
                        skipped++; return@forEach
                    }

                    // ← only neighborhoods/suburbs/quarters
                    val placeTag = props["place"]?.jsonPrimitive?.contentOrNull
                    if (placeTag == null || placeTag !in NEIGHBORHOOD_CATEGORIES) {
                        filteredOut++
                        return@forEach
                    }

                    val (lat, lon) = extractCoordinates(feature.geometry) ?: run {
                        skipped++; return@forEach
                    }

                    val description = props["description"]?.jsonPrimitive?.contentOrNull
                        ?: "Naselje u Sarajevu."
                    val id = feature.id.replace("/", "-")

                    try {
                        Locations.insertIgnore {
                            it[Locations.id] = id
                            it[Locations.name] = name
                            it[Locations.description] = description
                            it[Locations.latitude] = lat
                            it[Locations.longitude] = lon
                            it[Locations.category] = placeTag
                            it[Locations.address] = null
                            it[Locations.openingHours] = null
                            it[Locations.outdoorSeating] = false
                        }
                        inserted++
                    } catch (e: Exception) {
                        skipped++
                    }
                }
            }
            println("PlaceGeoJsonSeeder DONE: inserted=$inserted, skipped=$skipped, filteredOut=$filteredOut")

        } catch (e: Exception) {
            println("PlaceGeoJsonSeeder failed: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun extractCoordinates(geometry: GeoJsonGeometry?): Pair<Double, Double>? {
        if (geometry == null) return null

        geometry.center?.let { return it.lat to it.lon }

        val coords = geometry.coordinates ?: return null

        return try {
            when (geometry.type) {
                "Point" -> {
                    val text = coords.toString().removeSurrounding("[", "]")
                    val parts = text.split(",")
                    Pair(parts[1].trim().toDouble(), parts[0].trim().toDouble())
                }
                "Polygon", "MultiPolygon" -> {
                    val text = coords.toString()
                    val firstBracket = text.indexOf("[[")
                    val firstComma = text.indexOf(",", firstBracket)
                    val lon = text.substring(firstBracket + 2, firstComma).trim().toDouble()
                    val endBracket = text.indexOf("]", firstComma)
                    val lat = text.substring(firstComma + 1, endBracket).trim().toDouble()
                    Pair(lat, lon)
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}