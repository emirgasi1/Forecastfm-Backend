package com.example.auth

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.model.AuthResponse
import com.example.auth.model.ForgotPasswordRequest
import com.example.auth.model.LoginRequest
import com.example.auth.model.RefreshTokenRequest
import com.example.auth.model.RegisterRequest
import com.example.auth.model.ResetPasswordRequest
import com.example.routes.authRoutes
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.header
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
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthRoutesTest {

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
    }

    @Test
    fun `POST auth register returns 201 Created on valid input`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { authRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "newuser@example.com",
                    username = "newuser",
                    password = "password123",
                    bio = "Hello world"
                )
            )
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val body: AuthResponse = response.body()
        assertNotNull(body.token)
        assertEquals("newuser@example.com", body.user.email)
    }

    @Test
    fun `POST auth register returns 400 BadRequest on invalid email`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { authRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "invalid-email",
                    username = "user",
                    password = "password123",
                    bio = null
                )
            )
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `POST auth register returns 409 Conflict when user already exists`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { authRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val request = RegisterRequest(
            email = "duplicate@example.com",
            username = "dupuser",
            password = "password123",
            bio = null
        )

        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val secondResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.Conflict, secondResponse.status)
    }

    @Test
    fun `POST auth login returns 200 OK with valid credentials`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { authRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "login@example.com",
                    username = "loginuser",
                    password = "password123",
                    bio = null
                )
            )
        }

        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email = "login@example.com", password = "password123"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body: AuthResponse = response.body()
        assertTrue(body.token.isNotEmpty())
    }

    @Test
    fun `POST auth login returns 401 Unauthorized with invalid password`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { authRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "login2@example.com",
                    username = "loginuser2",
                    password = "password123",
                    bio = null
                )
            )
        }

        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email = "login2@example.com", password = "wrongpassword"))
        }

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST auth logout returns 200 OK when bearer token present`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { authRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val regRes = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "logout@example.com",
                    username = "logoutuser",
                    password = "password123",
                    bio = null
                )
            )
        }
        val token = regRes.body<AuthResponse>().token

        val response = client.post("/auth/logout") {
            header("Authorization", "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `POST auth logout returns 401 Unauthorized when token missing`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { authRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.post("/auth/logout")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `POST auth refresh returns 200 OK with valid refresh token`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { authRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val regRes = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(
                RegisterRequest(
                    email = "refresh@example.com",
                    username = "refreshuser",
                    password = "password123",
                    bio = null
                )
            )
        }
        val refreshToken = regRes.body<AuthResponse>().refreshToken

        val response = client.post("/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(refreshToken = refreshToken))
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `POST auth forgot-password returns 200 OK`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { authRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.post("/auth/forgot-password") {
            contentType(ContentType.Application.Json)
            setBody(ForgotPasswordRequest(email = "anyone@example.com"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `POST auth reset-password returns 400 BadRequest on short password`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { authRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.post("/auth/reset-password") {
            contentType(ContentType.Application.Json)
            setBody(ResetPasswordRequest(token = "some-token", newPassword = "123"))
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}