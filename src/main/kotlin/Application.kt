package com.example

import com.example.configure.createHttpClient
import com.example.database.DatabaseFactory
import com.example.weather.WeatherApi
import com.example.weather.WeatherRepository
import com.example.routes.weatherRoutes
import io.ktor.server.application.Application
import java.io.File

fun Application.rootModule() {

    DatabaseFactory.init()

    val httpClient = createHttpClient()

    val weatherApi = WeatherApi(httpClient)

    val weatherRepository = WeatherRepository(weatherApi)
    val uploadDir = File("uploads")
    if (!uploadDir.exists()) {
        uploadDir.mkdirs()
    }
    configureResources()
    configureSerialization()

    configureRouting(httpClient)

    weatherRoutes(weatherRepository)
}
fun Application.databaseModule() {
    DatabaseFactory.init()

    rootModule()
}


