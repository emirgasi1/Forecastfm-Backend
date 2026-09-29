package com.example.routes

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.database.table.PlaceRecommendations
import com.example.database.table.Places
import com.example.placerecommendation.CreatePlaceRecommendationRequest
import com.example.placerecommendation.PlaceRecommendation
import com.example.placerecommendation.PlaceRecommendationRepository
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PlaceRecommendationRoutesTest {

    @BeforeEach
    fun setup() {
        TestDatabase.initOnce()

        // Foreign keys safety: Delete child table FIRST, then parent table
        transaction {
            PlaceRecommendations.deleteAll()
            Places.deleteAll()
        }

        TestDataCleaner.clearAll()
    }

    private fun createDummyPlace(
        id: String,
        name: String = "Test Place",
        category: String = "Park"
    ) {
        transaction {
            Places.insert {
                it[Places.id] = id
                it[Places.name] = name
                it[Places.category] = category
                it[Places.venueId] = null
                it[Places.address] = "123 Test St"
                it[Places.latitude] = 0.0
                it[Places.longitude] = 0.0
                it[Places.description] = "Dummy description"
                it[Places.imageUrl] = null
                it[Places.rating] = 4.5
                it[Places.createdAt] = Instant.now()
            }
        }
    }

    @Test
    fun `POST api place-recommendations creates recommendation and returns 201 Created`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { placeRecommendationRoutes() }
        }
        val client = createClient {
            this.install(ClientContentNegotiation) { json() }
        }

        val placeId = "place_123"
        createDummyPlace(placeId, "Central Park Place", "Park")

        val request = CreatePlaceRecommendationRequest(
            name = "Central Park",
            category = "Park",
            location = "New York, NY",
            placeId = placeId,
            description = "Iconic urban park.",
            suitableFor = listOf("Families", "Tourists"),
            weatherCondition = "Sunny",
            ageGroup = listOf("All Ages"),
            rating = 4.8,
            imageUrl = "https://example.com/park.jpg"
        )

        val response = client.post("/api/place-recommendations") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val created: PlaceRecommendation = response.body()
        assertNotNull(created.id)
        assertEquals("Central Park", created.name)
        assertEquals("Park", created.category)
        assertEquals(4.8, created.rating)
    }

    @Test
    fun `GET api place-recommendations returns list with filters`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { placeRecommendationRoutes() }
        }
        val client = createClient {
            this.install(ClientContentNegotiation) { json() }
        }

        val placeId = "p1"
        createDummyPlace(placeId, "Museum Place", "Museum")

        val repository = PlaceRecommendationRepository()
        repository.createPlaceRecommendation(
            name = "Museum of Modern Art",
            category = "Museum",
            location = "NYC",
            placeId = placeId,
            description = "Art museum",
            suitableFor = listOf("Adults"),
            weatherCondition = "Rainy",
            ageGroup = listOf("Adults"),
            rating = 4.7,
            imageUrl = null
        )

        val response = client.get("/api/place-recommendations?category=Museum&weatherCondition=Rainy")

        assertEquals(HttpStatusCode.OK, response.status)
        val recommendations: List<PlaceRecommendation> = response.body()
        assertEquals(1, recommendations.size)
        assertEquals("Museum of Modern Art", recommendations[0].name)
    }

    @Test
    fun `GET api place-recommendations id returns 200 when found and 404 when missing`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { placeRecommendationRoutes() }
        }
        val client = createClient {
            this.install(ClientContentNegotiation) { json() }
        }

        val placeId = "p2"
        createDummyPlace(placeId, "Aquarium Place", "Attraction")

        val repository = PlaceRecommendationRepository()
        val created = repository.createPlaceRecommendation(
            name = "Aquarium",
            category = "Attraction",
            location = "Boston",
            placeId = placeId,
            description = "Sea life exhibit",
            suitableFor = listOf("Kids"),
            weatherCondition = "Any",
            ageGroup = listOf("Children"),
            rating = 4.5,
            imageUrl = null
        )

        val foundResponse = client.get("/api/place-recommendations/${created.id}")
        assertEquals(HttpStatusCode.OK, foundResponse.status)
        val fetched: PlaceRecommendation = foundResponse.body()
        assertEquals("Aquarium", fetched.name)

        val missingResponse = client.get("/api/place-recommendations/non-existent-id")
        assertEquals(HttpStatusCode.NotFound, missingResponse.status)
    }
}