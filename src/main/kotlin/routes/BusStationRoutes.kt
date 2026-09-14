package com.example.routes

import com.example.busstation.BusStationRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.busStationRoutes() {
    val repository = BusStationRepository()

    get("/api/bus-stations") {
        val stations = repository.getAllBusStations()
        call.respond(stations)
    }

    get("/api/bus-stations/{id}") {
        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ID")

        val station = repository.getBusStationById(id)
        if (station == null) {
            call.respond(HttpStatusCode.NotFound, "Bus station not found")
        } else {
            call.respond(station)
        }
    }
}