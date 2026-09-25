package com.example.weather

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.Serializable

@Serializable
data class WeatherApiComResponse(
    val location: WeatherApiComLocation,
    val current: WeatherApiComCurrent,
    val forecast: WeatherApiComForecast
)

@Serializable
data class WeatherApiComLocation(
    val name: String,
    val region: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val tz_id: String,
    val localtime_epoch: Long
)

@Serializable
data class WeatherApiComCurrent(
    val temp_c: Double,
    val feelslike_c: Double,
    val humidity: Int,
    val precip_mm: Double,
    val wind_kph: Double,
    val is_day: Int,
    val condition: WeatherApiComCondition
)

@Serializable
data class WeatherApiComCondition(
    val code: Int,
    val text: String
)

@Serializable
data class WeatherApiComForecast(
    val forecastday: List<WeatherApiComForecastDay>
)

@Serializable
data class WeatherApiComForecastDay(
    val date: String,
    val day: WeatherApiComForecastDayDetails,
    val hour: List<WeatherApiComHour>
)

@Serializable
data class WeatherApiComForecastDayDetails(
    val maxtemp_c: Double,
    val mintemp_c: Double,
    val uv: Double,
    val condition: WeatherApiComCondition
)

@Serializable
data class WeatherApiComHour(
    val time: String,
    val temp_c: Double,
    val condition: WeatherApiComCondition
)

class WeatherApiComApi(
    private val client: HttpClient,
    private val apiKey: String
) {

    suspend fun getWeather(
        latitude: Double,
        longitude: Double
    ): OpenMeteoResponse {

        val raw: WeatherApiComResponse = client.get(
            "https://api.weatherapi.com/v1/forecast.json"
        ) {
            parameter("key", apiKey)
            parameter("q", "$latitude,$longitude")
            parameter("days", 3)
            parameter("aqi", "no")
            parameter("alerts", "no")
        }.body()

        return convert(raw)
    }

    private fun convert(raw: WeatherApiComResponse): OpenMeteoResponse {

        val current = OpenMeteoCurrent(
            temperature_2m = raw.current.temp_c,
            relative_humidity_2m = raw.current.humidity,
            apparent_temperature = raw.current.feelslike_c,
            is_day = raw.current.is_day,
            precipitation = raw.current.precip_mm,
            weather_code = mapConditionToWmo(raw.current.condition.code),
            wind_speed_10m = raw.current.wind_kph / 3.6
        )

        val allHours = raw.forecast.forecastday.flatMap { it.hour }

        val hourly = OpenMeteoHourly(
            time = allHours.map { convertHourTime(it.time) },
            temperature_2m = allHours.map { it.temp_c },
            weather_code = allHours.map { mapConditionToWmo(it.condition.code) }
        )

        val daily = OpenMeteoDaily(
            time = raw.forecast.forecastday.map { it.date },
            weather_code = raw.forecast.forecastday.map {
                mapConditionToWmo(it.day.condition.code)
            },
            temperature_2m_max = raw.forecast.forecastday.map { it.day.maxtemp_c },
            temperature_2m_min = raw.forecast.forecastday.map { it.day.mintemp_c },
            uv_index_max = raw.forecast.forecastday.map { it.day.uv }
        )

        return OpenMeteoResponse(
            latitude = raw.location.lat,
            longitude = raw.location.lon,
            timezone = raw.location.tz_id,
            current = current,
            hourly = hourly,
            daily = daily
        )
    }

    private fun convertHourTime(weatherApiTime: String): String {
        return weatherApiTime.replace(" ", "T")
    }

    private fun mapConditionToWmo(code: Int): Int {
        return when (code) {
            1000 -> 0
            1003 -> 2
            1006 -> 3
            1009 -> 3
            1030 -> 45
            1063 -> 51
            1066 -> 71
            1069 -> 66
            1072 -> 56
            1087 -> 95
            1114 -> 73
            1117 -> 75
            1135 -> 45
            1147 -> 45
            1150 -> 51
            1153 -> 53
            1168 -> 56
            1171 -> 57
            1180 -> 61
            1183 -> 61
            1186 -> 63
            1189 -> 63
            1192 -> 65
            1195 -> 65
            1198 -> 66
            1201 -> 67
            1204 -> 66
            1207 -> 67
            1210 -> 71
            1213 -> 71
            1216 -> 73
            1219 -> 73
            1222 -> 75
            1225 -> 75
            1237 -> 77
            1240 -> 80
            1243 -> 81
            1246 -> 82
            1249 -> 85
            1252 -> 86
            1255 -> 71
            1258 -> 73
            1261 -> 77
            1264 -> 77
            1273 -> 95
            1276 -> 96
            1279 -> 71
            1282 -> 73
            else -> 0
        }
    }
}