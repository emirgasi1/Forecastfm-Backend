package com.example.routes

import com.example.weather.WeatherRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.weatherRoutes(
    weatherRepository: WeatherRepository
) {

    routing {

        get("/api/weather") {

            val location =
                call.request.queryParameters["location"]

            val latitude =
                call.request.queryParameters["latitude"]?.toDoubleOrNull()

            val longitude =
                call.request.queryParameters["longitude"]?.toDoubleOrNull()


            if (
                location == null ||
                latitude == null ||
                longitude == null
            ) {

                call.respond(
                    HttpStatusCode.BadRequest,
                    "Location, latitude and longitude are required"
                )

                return@get
            }


            val weather =
                weatherRepository.getWeather(
                    location = location,
                    latitude = latitude,
                    longitude = longitude
                )

            call.respond(weather)
        }
    }
}