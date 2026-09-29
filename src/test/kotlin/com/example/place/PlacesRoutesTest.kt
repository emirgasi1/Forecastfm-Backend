package com.example.routes

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.database.table.Locations
import com.example.database.table.Places
import com.example.place.Place
import database.table.Users
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlacesRoutesTest {

    private val userId = UUID.randomUUID().toString()

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        transaction {
            Users.insert {
                it[Users.id] = userId
                it[Users.email] = "test@example.com"
                it[Users.username] = "tester"
                it[Users.passwordHash] = "hash"
                it[Users.isVerified] = true
                it[Users.createdAt] = LocalDateTime.now()
                it[Users.status] = "ACTIVE"
            }
            Locations.insert {
                it[Locations.id] = "loc-1"
                it[Locations.name] = "Old Town"
                it[Locations.description] = "Historic"
                it[Locations.latitude] = 43.0
                it[Locations.longitude] = 18.0
            }
            Places.insert {
                it[Places.id] = "place-1"
                it[Places.name] = "Sebilj Fountain"
                it[Places.category] = "Landmark"
                it[Places.venueId] = "loc-1"
                it[Places.address] = "Brajiceva"
                it[Places.latitude] = 43.0
                it[Places.longitude] = 18.0
                it[Places.description] = "Wooden fountain"
                it[Places.rating] = 4.9
                it[Places.createdAt] = Instant.now()
            }
        }
    }

    @Test
    fun `GET api places returns all places`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { placesRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/places")
        assertEquals(HttpStatusCode.OK, response.status)
        val list: List<Place> = response.body()
        assertEquals(1, list.size)
        assertEquals("Sebilj Fountain", list[0].name)
    }

    @Test
    fun `GET api places by id returns place or 404`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { placesRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val okRes = client.get("/api/places/place-1")
        assertEquals(HttpStatusCode.OK, okRes.status)
        val place: Place = okRes.body()
        assertEquals("Sebilj Fountain", place.name)

        val notFoundRes = client.get("/api/places/invalid-id")
        assertEquals(HttpStatusCode.NotFound, notFoundRes.status)
    }

    @Test
    fun `POST and GET and DELETE place save endpoints work`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { placesRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val saveRes = client.post("/api/places/place-1/save") {
            header("User-Id", userId)
        }
        assertEquals(HttpStatusCode.Created, saveRes.status)

        val checkRes = client.get("/api/places/place-1/save") {
            header("User-Id", userId)
        }
        assertEquals(HttpStatusCode.OK, checkRes.status)
        val bodyMap: Map<String, Boolean> = checkRes.body()
        assertTrue(bodyMap["saved"] == true)

        val deleteRes = client.delete("/api/places/place-1/save") {
            header("User-Id", userId)
        }
        assertEquals(HttpStatusCode.OK, deleteRes.status)
    }
}