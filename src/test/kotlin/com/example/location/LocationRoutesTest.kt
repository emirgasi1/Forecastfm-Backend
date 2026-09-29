package com.example.routes

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.database.table.Locations
import com.example.location.Location
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocationRoutesTest {

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
    }

    private fun insertTestLocation(
        id: String,
        name: String,
        description: String,
        latitude: Double,
        longitude: Double
    ) {
        transaction {
            Locations.insert {
                it[Locations.id] = id
                it[Locations.name] = name
                it[Locations.description] = description
                it[Locations.latitude] = latitude
                it[Locations.longitude] = longitude
            }
        }
    }

    @Test
    fun `GET api locations returns 200 OK and empty list when no data`() = testApplication {
        application {
            this.install(ServerContentNegotiation) {
                json()
            }
            routing {
                locationRoutes()
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val response = client.get("/api/locations")

        assertEquals(HttpStatusCode.OK, response.status)

        val locations: List<Location> = response.body()
        assertTrue(locations.isEmpty())
    }

    @Test
    fun `GET api locations returns 200 OK with list of locations`() = testApplication {
        insertTestLocation("loc-1", "Bascarsija", "Old town", 43.8598, 18.4313)
        insertTestLocation("loc-2", "Vjecnica", "City Hall", 43.8591, 18.4334)

        application {
            this.install(ServerContentNegotiation) {
                json()
            }
            routing {
                locationRoutes()
            }
        }

        val client = createClient {
            install(ClientContentNegotiation) {
                json()
            }
        }

        val response = client.get("/api/locations")

        assertEquals(HttpStatusCode.OK, response.status)

        val locations: List<Location> = response.body()
        assertEquals(2, locations.size)

        assertEquals("loc-1", locations[0].id)
        assertEquals("Bascarsija", locations[0].name)
        assertEquals("loc-2", locations[1].id)
        assertEquals("Vjecnica", locations[1].name)
    }
}