package com.example.users

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.database.table.Posts
import com.example.database.table.SavedPosts
import com.example.routes.userRoutes
import com.example.user.UpdateProfileRequest
import com.example.user.UserRepository
import com.example.user.UserResponse
import database.table.Users
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserRoutesTest {

    @BeforeEach
    fun setup() {
        TestDatabase.initOnce()

        transaction {
            SavedPosts.deleteAll()
            Posts.deleteAll()
            Users.deleteAll()
        }

        TestDataCleaner.clearAll()
    }

    @Test
    fun `GET api users id returns 200 OK and user data when user exists`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { userRoutes() }
        }
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val repository = UserRepository()
        val userId = repository.createUser(
            email = "john@example.com",
            username = "johndoe",
            passwordHash = "hashed_pass",
            bio = "Hello world",
            profileImageUrl = "/uploads/avatar.jpg",
            favoriteLocation = "New York"
        )

        val response = client.get("/api/users/$userId")

        assertEquals(HttpStatusCode.OK, response.status)
        val userResponse: UserResponse = response.body()
        assertEquals(userId, userResponse.id)
        assertEquals("johndoe", userResponse.username)
        assertEquals("Hello world", userResponse.bio)
        assertEquals("/uploads/avatar.jpg", userResponse.profileImageUrl)
        assertEquals("New York", userResponse.favoriteLocation)
    }

    @Test
    fun `GET api users id returns 404 NotFound when user does not exist`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { userRoutes() }
        }
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val response = client.get("/api/users/non-existent-user-id")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET api users userId saved-posts returns 200 OK`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { userRoutes() }
        }
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val repository = UserRepository()
        val userId = repository.createUser("test@example.com", "testuser", "pass")

        val response = client.get("/api/users/$userId/saved-posts")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET api users userId posts returns 200 OK`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { userRoutes() }
        }
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val repository = UserRepository()
        val userId = repository.createUser("test2@example.com", "testuser2", "pass")

        val response = client.get("/api/users/$userId/posts")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `GET api users userId profile returns 200 OK when user exists`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { userRoutes() }
        }
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val repository = UserRepository()
        val userId = repository.createUser("profile@example.com", "profileuser", "pass")

        val response = client.get("/api/users/$userId/profile")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `PUT api users id updates user profile successfully`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { userRoutes() }
        }
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val repository = UserRepository()
        val userId = repository.createUser("update@example.com", "oldname", "pass")

        val updateRequest = UpdateProfileRequest(
            username = "newname",
            bio = "Updated bio",
            favoriteLocation = "Tokyo"
        )

        val response = client.put("/api/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(updateRequest)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val updatedUser = repository.getUserById(userId)
        assertNotNull(updatedUser)
        assertEquals("newname", updatedUser.username)
        assertEquals("Updated bio", updatedUser.bio)
        assertEquals("Tokyo", updatedUser.favoriteLocation)
    }

    @Test
    fun `PUT api users id returns 404 NotFound when user does not exist`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { userRoutes() }
        }
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val updateRequest = UpdateProfileRequest(
            username = "ghost",
            bio = "n/a",
            favoriteLocation = "n/a"
        )

        val response = client.put("/api/users/missing-id") {
            contentType(ContentType.Application.Json)
            setBody(updateRequest)
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST api users id profile-image uploads image and updates profile image URL`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { userRoutes() }
        }
        val client = createClient {
            install(ClientContentNegotiation) { json() }
        }

        val repository = UserRepository()
        val userId = repository.createUser("upload@example.com", "uploaduser", "pass")

        val imageBytes = "fake image content".toByteArray()

        val response = client.submitFormWithBinaryData(
            url = "/api/users/$userId/profile-image",
            formData = formData {
                append("image", imageBytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"profile.jpg\"")
                })
            }
        )

        assertEquals(HttpStatusCode.OK, response.status)
        val body: Map<String, String> = response.body()
        val imageUrl = body["url"]
        assertNotNull(imageUrl)
        assertTrue(imageUrl.startsWith("/uploads/"))

        val updatedUser = repository.getUserById(userId)
        assertNotNull(updatedUser)
        assertEquals(imageUrl, updatedUser.profileImageUrl)

        val filename = imageUrl.removePrefix("/uploads/")
        val savedFile = File("uploads", filename)
        if (savedFile.exists()) {
            savedFile.delete()
        }
    }
}