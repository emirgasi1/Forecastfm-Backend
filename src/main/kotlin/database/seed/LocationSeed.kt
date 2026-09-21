package com.example.database.seed

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
private data class GeoJsonFile(
    val features: List<GeoJsonFeature> = emptyList()
)

@Serializable
private data class GeoJsonFeature(
    val properties: GeoJsonProperties,
    val geometry: GeoJsonGeometry
)

@Serializable
private data class GeoJsonProperties(
    val name: String? = null
)

@Serializable
private data class GeoJsonGeometry(
    val type: String,
    val coordinates: List<Double>
)

private val EXCLUDED_NAMES = setOf(
    "B-faza",
    "Faza A",
    "Faza C",
    "Kvadrant C5",
    "Naselje Nova otoka",
    "Alipašino polje"
)

fun main() {
    val file = File("C:/Users/emirg/Desktop/sarajevo_neighborhoods.geojson")
    if (!file.exists()) {
        println("File not found: ${file.absolutePath}")
        return
    }

    val json = Json { ignoreUnknownKeys = true }
    val data = json.decodeFromString<GeoJsonFile>(file.readText())

    val filtered = data.features.filter {
        !it.properties.name.isNullOrBlank() && it.properties.name !in EXCLUDED_NAMES
    }
    val sorted = filtered.sortedBy { it.properties.name }

    val seenIds = mutableMapOf<String, Int>()

    val output = buildString {
        appendLine("        val osmLocations = listOf(")
        sorted.forEach { feature ->
            val rawName = feature.properties.name!!
            val name = rawName.replace("\\", "\\\\").replace("\"", "\\\"")
            val baseId = "osm-loc-" + slugify(rawName)

            val id = if (seenIds.containsKey(baseId)) {
                val count = seenIds[baseId]!! + 1
                seenIds[baseId] = count
                "$baseId-$count"
            } else {
                seenIds[baseId] = 1
                baseId
            }

            val lng = feature.geometry.coordinates[0]
            val lat = feature.geometry.coordinates[1]

            appendLine("            LocationSeed(\"$id\", \"$name\", \"Naselje u Sarajevu.\", $lat, $lng),")
        }
        appendLine("        )")
    }

    val outFile = File("C:/Users/emirg/Desktop/location_seeds.txt")
    outFile.writeText(output)

    println("Total features: ${data.features.size}")
    println("Usable (named + not excluded): ${filtered.size}")
    println("Unique IDs generated: ${seenIds.values.sum()}")
    println("Written to: ${outFile.absolutePath}")
}

private fun slugify(input: String): String {
    return input
        .lowercase()
        .replace("č", "c")
        .replace("ć", "c")
        .replace("š", "s")
        .replace("ž", "z")
        .replace("đ", "d")
        .replace(" ", "-")
        .replace(Regex("[^a-z0-9-]"), "")
        .replace(Regex("-+"), "-")
        .trim('-')
}