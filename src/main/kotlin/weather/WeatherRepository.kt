package com.example.weather

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class WeatherRepository(
    private val weatherApi: WeatherApi
) {

    suspend fun getWeather(
        location: String,
        latitude: Double,
        longitude: Double
    ): WeatherResponse {



        val response = weatherApi.getWeather(
            latitude = latitude,
            longitude = longitude
        )

        val airQualityResponse = weatherApi.getAirQuality(
            latitude = latitude,
            longitude = longitude
        )

        val currentHourIndex = findCurrentHourIndex(
            response.hourly.time
        )

        val hourly = response.hourly.time
            .indices
            .drop(currentHourIndex)
            .take(5)
            .map { index ->

                WeatherForecastResponse(
                    time = formatHour(
                        response.hourly.time[index]
                    ),
                    temperature =
                        "${response.hourly.temperature_2m[index]}°C",
                    condition =
                        weatherCodeToCondition(
                            response.hourly.weather_code[index]
                        )
                )
            }

        val daily = response.daily.time
            .indices
            .take(5)
            .map { index ->

                WeatherForecastResponse(
                    time = formatDay(
                        response.daily.time[index],
                        index
                    ),
                    temperature =
                        "${response.daily.temperature_2m_max[index]}°C",
                    condition =
                        weatherCodeToCondition(
                            response.daily.weather_code[index]
                        )
                )
            }

        val airQualityHourIndex = findCurrentHourIndex(
            airQualityResponse.hourly.time
        )

        val pm25 = airQualityResponse.hourly.pm2_5
            .getOrNull(airQualityHourIndex)

        return WeatherResponse(

            location = location,

            temperature =
                "${response.current.temperature_2m}°C",

            condition =
                weatherCodeToCondition(
                    response.current.weather_code
                ),

            feelsLike =
                "${response.current.apparent_temperature}°C",

            humidity =
                "${response.current.relative_humidity_2m}%",

            wind =
                "${response.current.wind_speed_10m} km/h",

            uvIndex =
                response.daily.uv_index_max
                    .firstOrNull()
                    ?.toString()
                    ?: "N/A",

            airQuality =
                pm25?.toString() ?: "N/A",

            hourly = hourly,

            daily = daily
        )
    }


    private fun findCurrentHourIndex(
        times: List<String>
    ): Int {

        val currentHour =
            LocalDateTime.now()
                .withMinute(0)
                .withSecond(0)
                .withNano(0)

        return times.indexOfFirst { time ->

            LocalDateTime.parse(time) >= currentHour

        }.coerceAtLeast(0)
    }


    private fun formatHour(
        time: String
    ): String {

        return LocalDateTime
            .parse(time)
            .format(
                DateTimeFormatter.ofPattern("HH:mm")
            )
    }


    private fun formatDay(
        date: String,
        index: Int
    ): String {

        if (index == 0) {
            return "Today"
        }

        return LocalDate
            .parse(date)
            .format(
                DateTimeFormatter.ofPattern("EEE")
            )
    }


    private fun weatherCodeToCondition(
        code: Int
    ): String {

        return when (code) {

            0 -> "Sunny"

            1, 2 -> "Partly Cloudy"

            3 -> "Cloudy"

            45, 48 -> "Foggy"

            51, 53, 55,
            56, 57 -> "Drizzle"

            61, 63, 65,
            66, 67,
            80, 81, 82 -> "Rain"

            71, 73, 75,
            77,
            85, 86 -> "Snow"

            95, 96, 99 -> "Thunderstorm"

            else -> "Unknown"
        }
    }
}