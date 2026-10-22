package com.example.location

import com.example.database.table.Locations
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
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
    val coordinates: JsonElement
)

object PlaceGeoJsonSeeder {

    fun seed() {
        // This reads the file directly from the src/main/resources folder inside the JAR
        val existing = transaction { Locations.selectAll().count() }
        if (existing > 0L) {
            println("PlaceGeoJsonSeeder: Locations already present ($existing) — skipping")
            return
        }
        val inputStream = PlaceGeoJsonSeeder::class.java.classLoader.getResourceAsStream("sarajevo_places.geojson")

        if (inputStream == null) {
            println("PlaceGeoJsonSeeder: File 'sarajevo_places.geojson' NOT FOUND in resources!")
            return
        }

        println("PlaceGeoJsonSeeder: Starting...")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val json = Json { ignoreUnknownKeys = true }

        try {
            val featureCollection = json.decodeFromString<GeoJsonFeatureCollection>(jsonString)
            println("PlaceGeoJsonSeeder: ${featureCollection.features.size} features to process")

            var inserted = 0
            var skipped = 0

            transaction {
                featureCollection.features.forEach { feature ->

                    val props = feature.properties
                    val name = props?.get("name")?.jsonPrimitive?.contentOrNull ?: return@forEach

                    val category = props["amenity"]?.jsonPrimitive?.contentOrNull
                        ?: props["tourism"]?.jsonPrimitive?.contentOrNull
                        ?: props["highway"]?.jsonPrimitive?.contentOrNull
                        ?: props["place"]?.jsonPrimitive?.contentOrNull
                        ?: "general"

                    val street = props["addr:street"]?.jsonPrimitive?.contentOrNull
                    val housenumber = props["addr:housenumber"]?.jsonPrimitive?.contentOrNull
                    val address = if (street != null) {
                        if (housenumber != null) "$street $housenumber" else street
                    } else null

                    val openingHours = props["opening_hours"]?.jsonPrimitive?.contentOrNull
                    val outdoorSeating = props["outdoor_seating"]?.jsonPrimitive?.contentOrNull == "yes"

                    val (lat, lon) = extractCoordinates(feature.geometry) ?: return@forEach
                    val description = props["description"]?.jsonPrimitive?.contentOrNull ?: "Sarajevo location"
                    val id = feature.id.replace("/", "-")

                    try {
                        Locations.insertIgnore {
                            it[Locations.id] = id
                            it[Locations.name] = name
                            it[Locations.description] = description
                            it[Locations.latitude] = lat
                            it[Locations.longitude] = lon
                            it[Locations.category] = category
                            it[Locations.address] = address
                            it[Locations.openingHours] = openingHours
                            it[Locations.outdoorSeating] = outdoorSeating
                        }
                        inserted++
                    } catch (e: Exception) {
                        skipped++
                    }
                }
            }
            println("PlaceGeoJsonSeeder DONE: inserted=$inserted, skipped=$skipped")

        } catch (e: Exception) {
            println("PlaceGeoJsonSeeder failed: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun extractCoordinates(geometry: GeoJsonGeometry?): Pair<Double, Double>? {
        if (geometry == null) return null
        return try {
            when (geometry.type) {
                "Point" -> {
                    val coords = geometry.coordinates.toString().removeSurrounding("[", "]").split(",")
                    Pair(coords[1].trim().toDouble(), coords[0].trim().toDouble())
                }
                "Polygon", "MultiPolygon" -> {
                    val text = geometry.coordinates.toString()
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

    private val JsonElement?.contentOrNull: String?
        get() = try { this?.jsonPrimitive?.content } catch (e: Exception) { null }

    private val JsonElement?.jsonPrimitive: kotlinx.serialization.json.JsonPrimitive
        get() = this as kotlinx.serialization.json.JsonPrimitive
}