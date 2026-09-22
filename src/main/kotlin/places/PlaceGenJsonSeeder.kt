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
    private const val NEAREST_LOCATION_MAX_METERS = 200.0

    private val json = Json { ignoreUnknownKeys = true }

    fun seed() {
        val text = PlaceGeoJsonSeeder::class.java
            .getResourceAsStream(RESOURCE_PATH)
            ?.bufferedReader()
            ?.use { it.readText() }
            ?: return

        val root = json.parseToJsonElement(text).jsonObject
        val features = root["features"]?.jsonArray ?: return

        val locations = transaction {
            Locations.selectAll().map {
                LocationRow(
                    id = it[Locations.id],
                    latitude = it[Locations.latitude],
                    longitude = it[Locations.longitude]
                )
            }
        }

        for (feature in features) {
            try {
                insertFeature(feature.jsonObject, locations)
            } catch (_: Exception) {
            }
        }
    }

    private data class LocationRow(val id: String, val latitude: Double, val longitude: Double)

    private fun insertFeature(feature: JsonObject, locations: List<LocationRow>) {
        val props = feature["properties"]?.jsonObject ?: return
        val osmId = props["osm_id"]?.jsonPrimitive?.contentOrNull ?: return
        val fclass = props["fclass"]?.jsonPrimitive?.contentOrNull ?: return
        val name = props["name"]?.jsonPrimitive?.contentOrNull ?: fclass

        val category = FclassCategoryMapper.categoryFor(fclass) ?: return

        val centroid = centroidOf(feature["geometry"]?.jsonObject ?: return) ?: return
        val placeId = "osm-$fclass-$osmId"

        val nearest = locations.minByOrNull {
            haversineMeters(centroid.first, centroid.second, it.latitude, it.longitude)
        }
        val venueId = nearest?.let {
            val d = haversineMeters(centroid.first, centroid.second, it.latitude, it.longitude)
            if (d <= NEAREST_LOCATION_MAX_METERS) it.id else null
        }

        val address = nearest?.id ?: "Sarajevo"
        val description = "$category in Sarajevo"

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
                it[Places.venueId] = venueId
                it[Places.address] = address
                it[Places.latitude] = centroid.first
                it[Places.longitude] = centroid.second
                it[Places.description] = description
                it[Places.imageUrl] = null
                it[Places.rating] = 0.0
                it[Places.createdAt] = Instant.now()
            }
        }
    }

    private fun centroidOf(geometry: JsonObject): Pair<Double, Double>? {
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