package com.example.weather

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class WeatherApi(
    private val client: HttpClient
) {

    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): OpenMeteoResponse {

        return client.get(
            "https://api.open-meteo.com/v1/forecast"
        ) {

            parameter("latitude", latitude)
            parameter("longitude", longitude)

            parameter(
                "current",
                "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,weather_code,wind_speed_10m"
            )

            parameter(
                "hourly",
                "temperature_2m,weather_code"
            )

            parameter(
                "daily",
                "weather_code,temperature_2m_max,temperature_2m_min,uv_index_max"
            )

            parameter("timezone", "auto")
        }.body()
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

            parameter(
                "hourly",
                "pm10,pm2_5"
            )

            parameter("timezone", "auto")

        }.body()
    }
}