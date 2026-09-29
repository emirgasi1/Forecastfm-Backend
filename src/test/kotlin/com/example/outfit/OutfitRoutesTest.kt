package com.example.outfit

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.database.table.Outfits
import com.example.routes.outfitRoutes
import database.table.Users
import database.table.Users.email
import database.table.Users.passwordHash
import database.table.Users.username
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class OutfitRoutesTest {

    @BeforeEach
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        transaction {
            // 1. Clear tables in reverse dependency order to satisfy foreign keys
            Outfits.deleteAll()
            Users.deleteAll()

            // 2. Seed required users referenced across test scenarios
            listOf("u1", "u2", "user_123", "user_abc", "user_xyz").forEach { userId ->
                Users.insert { row ->
                    row[id] = userId
                    row[email] = "$userId@example.com"
                    row[username] = "user_$userId"
                    row[passwordHash] = "hashed_pass"
                }
            }
        }
    }

    @AfterEach
    fun tearDown() {
        File("uploads").deleteRecursively()
    }

    @Test
    fun `POST api outfits creates outfit and returns 201 Created`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { outfitRoutes() }
        }
        val client = createClient {
            this.install(ClientContentNegotiation) { json() }
        }

        val request = CreateOutfitRequest(
            userId = "user_123",
            imageUrl = "https://example.com/jacket.jpg",
            title = "Winter Puffer Jacket",
            weatherCondition = "Snowy",
            season = "Winter",
            storeName = "Zara",
            price = "$120.00"
        )

        val response = client.post("/api/outfits") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val created: Outfit = response.body()
        assertNotNull(created.id)
        assertEquals("user_123", created.userId)
        assertEquals("Winter Puffer Jacket", created.title)
        assertEquals("Snowy", created.weatherCondition)
        assertEquals("Zara", created.storeName)
    }

    @Test
    fun `GET api outfits trending returns list of trending outfits`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { outfitRoutes() }
        }
        val client = createClient {
            this.install(ClientContentNegotiation) { json() }
        }

        val repository = OutfitRepository()
        val o1 = repository.createOutfit("u1", "img1", "Outfit 1", "Sunny", "Summer")
        val o2 = repository.createOutfit("u2", "img2", "Outfit 2", "Sunny", "Summer")
        repository.likeOutfit(o2.id)

        val response = client.get("/api/outfits/trending")

        assertEquals(HttpStatusCode.OK, response.status)
        val outfits: List<Outfit> = response.body()
        assertEquals(2, outfits.size)
        assertEquals(o2.id, outfits[0].id)
    }

    @Test
    fun `GET api outfits weather weather returns filtered outfits`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { outfitRoutes() }
        }
        val client = createClient {
            this.install(ClientContentNegotiation) { json() }
        }

        val repository = OutfitRepository()
        repository.createOutfit("u1", "img1", "Raincoat", "Rainy", "Spring")
        repository.createOutfit("u2", "img2", "T-Shirt", "Sunny", "Summer")

        val response = client.get("/api/outfits/weather/Rainy")

        assertEquals(HttpStatusCode.OK, response.status)
        val outfits: List<Outfit> = response.body()
        assertEquals(1, outfits.size)
        assertEquals("Raincoat", outfits[0].title)
    }

    @Test
    fun `POST api outfits id like increments outfit likes`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { outfitRoutes() }
        }
        val client = createClient {
            this.install(ClientContentNegotiation) { json() }
        }

        val repository = OutfitRepository()
        val outfit = repository.createOutfit("u1", "img1", "Coat", "Cold", "Winter")

        val response = client.post("/api/outfits/${outfit.id}/like")
        assertEquals(HttpStatusCode.OK, response.status)

        val updated = repository.getOutfitById(outfit.id)
        assertNotNull(updated)
        assertEquals(1, updated.likes)
    }

    @Test
    fun `POST and DELETE api outfits outfitId save handles saving and unsaving outfits`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { outfitRoutes() }
        }
        val client = createClient {
            this.install(ClientContentNegotiation) { json() }
        }

        val repository = OutfitRepository()
        val outfit = repository.createOutfit("u1", "img1", "Hoodie", "Cloudy", "Fall")

        // 1. Save Outfit
        val saveResponse = client.post("/api/outfits/${outfit.id}/save") {
            header("User-Id", "user_abc")
        }
        assertEquals(HttpStatusCode.Created, saveResponse.status)

        // 2. Check if saved
        val checkResponse = client.get("/api/outfits/${outfit.id}/save") {
            header("User-Id", "user_abc")
        }
        assertEquals(HttpStatusCode.OK, checkResponse.status)
        val checkResult: Map<String, Boolean> = checkResponse.body()
        assertTrue(checkResult["saved"] == true)

        // 3. Unsave Outfit
        val unsaveResponse = client.delete("/api/outfits/${outfit.id}/save") {
            header("User-Id", "user_abc")
        }
        assertEquals(HttpStatusCode.OK, unsaveResponse.status)

        // 4. Verify no longer saved
        val reCheckResponse = client.get("/api/outfits/${outfit.id}/save") {
            header("User-Id", "user_abc")
        }
        val reCheckResult: Map<String, Boolean> = reCheckResponse.body()
        assertFalse(reCheckResult["saved"] == true)
    }

    @Test
    fun `GET api outfits saved returns user saved outfits`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { outfitRoutes() }
        }
        val client = createClient {
            this.install(ClientContentNegotiation) { json() }
        }

        val outfitRepo = OutfitRepository()
        val savedRepo = SavedOutfitRepository()
        val outfit = outfitRepo.createOutfit("u1", "img1", "Denim Jacket", "Mild", "Spring")
        savedRepo.saveOutfit("user_xyz", outfit.id)

        val response = client.get("/api/outfits/saved") {
            header("User-Id", "user_xyz")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val savedOutfits: List<Outfit> = response.body()
        assertEquals(1, savedOutfits.size)
        assertEquals("Denim Jacket", savedOutfits[0].title)
    }

    @Test
    fun `GET api outfits id returns outfit when found and 404 when missing`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { outfitRoutes() }
        }
        val client = createClient {
            this.install(ClientContentNegotiation) { json() }
        }

        val repository = OutfitRepository()
        val outfit = repository.createOutfit("u1", "img1", "Boots", "Rainy", "Autumn")

        // Existing outfit
        val foundResponse = client.get("/api/outfits/${outfit.id}")
        assertEquals(HttpStatusCode.OK, foundResponse.status)
        val fetched: Outfit = foundResponse.body()
        assertEquals("Boots", fetched.title)

        // Missing outfit
        val missingResponse = client.get("/api/outfits/non-existent-id")
        assertEquals(HttpStatusCode.NotFound, missingResponse.status)
    }

    @Test
    fun `POST api uploads image processes multipart upload and returns image URL`() = testApplication {
        application {
            this.install(ServerContentNegotiation) { json() }
            routing { outfitRoutes() }
        }
        val client = createClient {
            this.install(ClientContentNegotiation) { json() }
        }

        val response = client.submitFormWithBinaryData(
            url = "/api/uploads/image",
            formData = formData {
                append("file", "fake-image-bytes".toByteArray(), Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"test-outfit.jpg\"")
                })
            }
        )

        assertEquals(HttpStatusCode.OK, response.status)
        val responseBody: Map<String, String> = response.body()
        assertNotNull(responseBody["url"])
        assertTrue(responseBody["url"]!!.startsWith("/uploads/"))
        assertTrue(responseBody["url"]!!.endsWith(".jpg"))
    }
}