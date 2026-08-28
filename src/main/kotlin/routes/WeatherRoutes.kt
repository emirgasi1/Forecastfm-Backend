package com.example.routes

import com.example.weather.WeatherRepository
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.weatherRoutes(
    weatherRepository: WeatherRepository
) {

    routing {

        get("/api/weather") {

            val weather = weatherRepository.getWeather()

            call.respond(weather)
        }
    }
}