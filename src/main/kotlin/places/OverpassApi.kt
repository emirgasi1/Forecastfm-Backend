package com.example.places

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode

class OverpassApi(
    private val client: HttpClient
) {

    suspend fun getCafesInSarajevo(): List<OsmPlace> {
        val query = """
            [out:json][timeout:25];
            (
              node["amenity"="cafe"](43.82,18.28,43.88,18.45);
              way["amenity"="cafe"](43.82,18.28,43.88,18.45);
            );
            out center;
        """.trimIndent()

        val response =  client.get("https://overpass.kumi.systems/api/interpreter") {
            header("User-Agent", "ForecastFM/1.0 (contact@forecastfm.demo)")
            parameter("data", query)
        }

        if (response.status != HttpStatusCode.OK) {
            val errorBody: String = try {
                response.body()
            } catch (e: Exception) {
                "unreadable"
            }
            throw Exception("Overpass API error: ${response.status} — $errorBody")
        }

        val body: OverpassResponse = response.body()
        return body.elements.mapNotNull { it.toOsmPlace() }
    }
}