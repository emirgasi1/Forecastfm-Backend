package com.example.weather

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class WeatherApi(
    private val client: HttpClient
) {

    private val fallbackApi: WeatherApiComApi? =
        System.getenv("WEATHER_API_KEY")
            ?.takeIf { it.isNotBlank() }
            ?.let { WeatherApiComApi(client, it) }

    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): OpenMeteoResponse {

        WeatherCache.getFresh(latitude, longitude)?.let { return it }

        tryOpenMeteo(latitude, longitude)?.let { fresh ->
            WeatherCache.put(latitude, longitude, fresh)
            return fresh
        }

        tryFallback(latitude, longitude)?.let { fresh ->
            WeatherCache.put(latitude, longitude, fresh)
            return fresh
        }

        WeatherCache.getStale(latitude, longitude)?.let { return it }

        throw IllegalStateException("Weather unavailable for $latitude,$longitude")
    }

    private suspend fun tryOpenMeteo(
        latitude: Double,
        longitude: Double
    ): OpenMeteoResponse? {
        return try {
            val response: OpenMeteoResponse = client.get(
                "https://api.open-meteo.com/v1/forecast"
            ) {
                parameter("latitude", latitude)
                parameter("longitude", longitude)
                parameter(
                    "current",
                    "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,wind_speed_10m"
                )
                parameter("hourly", "temperature_2m,weather_code")
                parameter(
                    "daily",
                    "weather_code,temperature_2m_max,temperature_2m_min,uv_index_max"
                )
                parameter("timezone", "auto")
            }.body()

            if (response.current.temperature_2m.isNaN()) null else response
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun tryFallback(
        latitude: Double,
        longitude: Double
    ): OpenMeteoResponse? {
        val api = fallbackApi ?: return null
        return try {
            api.getWeather(latitude, longitude)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getAirQuality(
        latitude: Double,
        longitude: Double
    ): OpenMeteoAirQualityResponse {
        return client.get(
            "https://air-quality-api.open-meteo.com/v1/air-quality"
        ) {
            parameter("latitude", latitude)
            parameter("longitude", longitude)
            parameter("hourly", "pm10,pm2_5")
            parameter("timezone", "auto")
        }.body()
    }
}