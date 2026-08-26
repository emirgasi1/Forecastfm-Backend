package com.example

import com.example.configure.createHttpClient
import com.example.database.DatabaseFactory
import com.example.user.UserRepository
import com.example.weather.WeatherApi
import com.example.weather.WeatherRepository
import com.example.weather.weatherRoutes
import io.ktor.server.application.Application

fun Application.rootModule() {

    DatabaseFactory.init()

    val httpClient = createHttpClient()

    val weatherApi = WeatherApi(httpClient)

    val weatherRepository = WeatherRepository(weatherApi)

    configureResources()
    configureSerialization()

    configureRouting(httpClient)

    weatherRoutes(weatherRepository)
}
fun Application.databaseModule() {
    DatabaseFactory.init()

    rootModule()
}


