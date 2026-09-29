package com.example.weather

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals

class WeatherRepositoryTest {

    @Test
    fun `getWeather maps OpenMeteo responses correctly into WeatherResponse`() = runBlocking {
        val nowIso = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).toString()

        val mockOpenMeteoJson = """
            {
              "latitude": 12.34,
              "longitude": 56.78,
              "timezone": "UTC",
              "current": {
                "time": "$nowIso",
                "interval": 900,
                "temperature_2m": 22.5,
                "relative_humidity_2m": 60,
                "apparent_temperature": 23.0,
                "is_day": 1,
                "precipitation": 0.0,
                "weather_code": 0,
                "wind_speed_10m": 12.0
              },
              "hourly": {
                "time": ["$nowIso"],
                "temperature_2m": [22.5],
                "weather_code": [0]
              },
              "daily": {
                "time": ["2026-06-06"],
                "weather_code": [0],
                "temperature_2m_max": [25.0],
                "temperature_2m_min": [15.0],
                "uv_index_max": [6.5]
              }
            }
        """.trimIndent()

        val mockAirQualityJson = """
            {
              "latitude": 12.34,
              "longitude": 56.78,
              "timezone": "UTC",
              "hourly": {
                "time": ["$nowIso"],
                "pm10": [10.5],
                "pm2_5": [5.2]
              }
            }
        """.trimIndent()

        val mockEngine = MockEngine { request ->
            val url = request.url.toString()
            val content = if (url.contains("air-quality")) {
                mockAirQualityJson
            } else {
                mockOpenMeteoJson
            }
            respond(
                content = content,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val client = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }

        val weatherApi = WeatherApi(client)
        val repository = WeatherRepository(weatherApi)

        val result = repository.getWeather(
            location = "Test City",
            latitude = 12.34,
            longitude = 56.78
        )

        assertEquals("Test City", result.location)
        assertEquals("22.5°C", result.temperature)
        assertEquals("Sunny", result.condition)
        assertEquals("23.0°C", result.feelsLike)
        assertEquals("60%", result.humidity)
        assertEquals("12.0 km/h", result.wind)
        assertEquals("6.5", result.uvIndex)
        assertEquals("5.2", result.airQuality)
        assertEquals(1, result.hourly.size)
        assertEquals(1, result.daily.size)
        assertEquals("Today", result.daily[0].time)
    }
}