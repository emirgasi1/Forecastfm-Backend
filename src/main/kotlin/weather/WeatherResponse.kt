package com.example.weather


import kotlinx.serialization.Serializable

@Serializable
data class WeatherResponse(
    val location: String,
    val temperature: String,
    val condition: String,
    val feelsLike: String,
    val humidity: String,
    val wind: String,
    val uvIndex: String,
    val airQuality: String,
    val hourly: List<WeatherForecastResponse>,
    val daily: List<WeatherForecastResponse>
)

@Serializable
data class WeatherForecastResponse(
    val time: String,
    val temperature: String,
    val condition: String
)