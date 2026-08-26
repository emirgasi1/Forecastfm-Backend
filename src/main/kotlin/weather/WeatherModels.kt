package com.example.weather

import kotlinx.serialization.Serializable

@Serializable
data class OpenMeteoResponse(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: OpenMeteoCurrent,
    val hourly: OpenMeteoHourly,
    val daily: OpenMeteoDaily
)

@Serializable
data class OpenMeteoCurrent(
    val temperature_2m: Double,
    val relative_humidity_2m: Int,
    val apparent_temperature: Double,
    val is_day: Int,
    val precipitation: Double,
    val weather_code: Int,
    val wind_speed_10m: Double
)

@Serializable
data class OpenMeteoHourly(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val weather_code: List<Int>
)

@Serializable
data class OpenMeteoDaily(
    val time: List<String>,
    val weather_code: List<Int>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val uv_index_max: List<Double>
)


@Serializable
data class OpenMeteoAirQualityResponse(
    val hourly: OpenMeteoAirQualityHourly
)

@Serializable
data class OpenMeteoAirQualityHourly(
    val time: List<String>,
    val pm10: List<Double?>,
    val pm2_5: List<Double?>
)

