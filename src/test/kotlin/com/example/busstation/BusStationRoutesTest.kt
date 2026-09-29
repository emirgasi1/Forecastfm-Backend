package com.example.busstation

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.routes.busStationRoutes
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class BusStationRoutesTest {

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
    }

    @Test
    fun `GET api bus-stations returns list of all stations`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { busStationRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val repo = BusStationRepository()
        repo.createBusStation("Terminal 1", 45.0, 15.0, listOf("Line 1"))
        repo.createBusStation("Terminal 2", 46.0, 16.0, listOf("Line 2"))

        val response = client.get("/api/bus-stations")

        assertEquals(HttpStatusCode.OK, response.status)
        val stations: List<BusStation> = response.body()
        assertEquals(2, stations.size)
    }

    @Test
    fun `GET api bus-stations id returns 200 OK with station when it exists`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { busStationRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val repo = BusStationRepository()
        val station = repo.createBusStation("Downtown Hub", 40.71, -74.00, listOf("Route A", "Route B"))

        val response = client.get("/api/bus-stations/${station.id}")

        assertEquals(HttpStatusCode.OK, response.status)
        val fetched: BusStation = response.body()
        assertEquals(station.id, fetched.id)
        assertEquals("Downtown Hub", fetched.name)
        assertEquals(listOf("Route A", "Route B"), fetched.lines)
    }

    @Test
    fun `GET api bus-stations id returns 404 NotFound when station does not exist`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { busStationRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/bus-stations/non-existent-id")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}