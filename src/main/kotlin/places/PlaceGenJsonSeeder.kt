package com.example.places

import com.example.database.table.Locations
import com.example.database.table.Places
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object PlaceGeoJsonSeeder {

    private const val RESOURCE_PATH = "/sarajevo_pois_food_shops.geojson"

    private val json = Json { ignoreUnknownKeys = true }

    fun seed() {
        println("PlaceGeoJsonSeeder: starting")

        val text = PlaceGeoJsonSeeder::class.java
            .getResourceAsStream(RESOURCE_PATH)
            ?.bufferedReader()
            ?.use { it.readText() }

        if (text == null) {
            println("PlaceGeoJsonSeeder: RESOURCE NOT FOUND at $RESOURCE_PATH")
            return
        }

        println("PlaceGeoJsonSeeder: resource loaded, ${text.length} chars")

        val root = json.parseToJsonElement(text).jsonObject
        val features = root["features"]?.jsonArray
        if (features == null) {
            println("PlaceGeoJsonSeeder: no features array")
            return
        }

        println("PlaceGeoJsonSeeder: ${features.size} features to process")

        val locations = transaction {
            Locations.selectAll().map {
                LocationRow(
                    id = it[Locations.id],
                    latitude = it[Locations.latitude],
                    longitude = it[Locations.longitude]
                )
            }
        }

        println("PlaceGeoJsonSeeder: ${locations.size} locations in DB")

        if (locations.isEmpty()) {
            println("PlaceGeoJsonSeeder: no locations, aborting")
            return
        }

        var inserted = 0
        var skippedExists = 0
        var failedParse = 0
        var failedInsert = 0

        for (feature in features) {
            try {
                val result = insertFeature(feature.jsonObject, locations)
                when (result) {
                    InsertResult.INSERTED -> inserted++
                    InsertResult.EXISTS -> skippedExists++
                    InsertResult.BAD_DATA -> failedParse++
                }
            } catch (e: Exception) {
                failedInsert++
                if (failedInsert <= 5) {
                    println("PlaceGeoJsonSeeder: insert FAILED -> ${e::class.simpleName}: ${e.message}")
                    e.printStackTrace()
                }
            }
        }

        println("PlaceGeoJsonSeeder DONE: inserted=$inserted existing=$skippedExists badData=$failedParse failedInsert=$failedInsert")
    }

    private data class LocationRow(val id: String, val latitude: Double, val longitude: Double)

    private enum class InsertResult { INSERTED, EXISTS, BAD_DATA }

    private fun insertFeature(feature: JsonObject, locations: List<LocationRow>): InsertResult {
        val props = feature["properties"]?.jsonObject ?: return InsertResult.BAD_DATA
        val osmId = props["osm_id"]?.jsonPrimitive?.contentOrNull
            ?: props["@id"]?.jsonPrimitive?.contentOrNull
            ?: return InsertResult.BAD_DATA
        val fclass = props["fclass"]?.jsonPrimitive?.contentOrNull
            ?: props["amenity"]?.jsonPrimitive?.contentOrNull
            ?: props["shop"]?.jsonPrimitive?.contentOrNull
            ?: return InsertResult.BAD_DATA
        val name = props["name"]?.jsonPrimitive?.contentOrNull ?: fclass

        val category = FclassCategoryMapper.categoryFor(fclass) ?: return InsertResult.BAD_DATA

        val centroid = centroidOf(feature["geometry"]?.jsonObject ?: return InsertResult.BAD_DATA)
            ?: return InsertResult.BAD_DATA
        val placeId = "osm-$fclass-$osmId"

        val nearest = locations.minByOrNull {
            haversineMeters(centroid.first, centroid.second, it.latitude, it.longitude)
        } ?: return InsertResult.BAD_DATA

        var result = InsertResult.EXISTS
        transaction {
            val exists = Places.selectAll()
                .where { Places.id eq placeId }
                .limit(1)
                .any()
            if (exists) return@transaction

            Places.insert {
                it[Places.id] = placeId
                it[Places.name] = name
                it[Places.category] = category
                it[Places.venueId] = nearest.id
                it[Places.address] = ""
                it[Places.latitude] = centroid.first
                it[Places.longitude] = centroid.second
                it[Places.description] = "$category in Sarajevo"
                it[Places.imageUrl] = null
                it[Places.rating] = 0.0
                it[Places.createdAt] = Instant.now()
            }
            result = InsertResult.INSERTED
        }
        return result
    }

    private fun centroidOf(geometry: JsonObject): Pair<Double, Double>? {
        val type = geometry["type"]?.jsonPrimitive?.contentOrNull

        if (type == "Point") {
            val coords = geometry["coordinates"]?.jsonArray ?: return null
            val lon = coords.getOrNull(0)?.jsonPrimitive?.doubleOrNull ?: return null
            val lat = coords.getOrNull(1)?.jsonPrimitive?.doubleOrNull ?: return null
            return Pair(lat, lon)
        }

        val coords = geometry["coordinates"]?.jsonArray ?: return null
        var sumLat = 0.0
        var sumLon = 0.0
        var count = 0

        fun walk(node: JsonArray) {
            for (el in node) {
                val arr = el as? JsonArray ?: continue
                if (arr.isNotEmpty() && arr[0] is JsonPrimitive && (arr[0] as JsonPrimitive).doubleOrNull != null) {
                    val lon = (arr[0] as JsonPrimitive).doubleOrNull ?: continue
                    val lat = (arr[1] as JsonPrimitive).doubleOrNull ?: continue
                    sumLon += lon
                    sumLat += lat
                    count++
                } else {
                    walk(arr)
                }
            }
        }

        walk(coords)
        if (count == 0) return null
        return Pair(sumLat / count, sumLon / count)
    }

    private fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}