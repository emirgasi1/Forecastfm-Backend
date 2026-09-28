package com.example.auth

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.AuthRepository
import com.example.auth.ConflictException
import com.example.auth.ValidationException
import com.example.auth.model.RegisterRequest
import database.table.Users
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthRepositoryTest {

    private lateinit var repository: AuthRepository

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
        repository = AuthRepository()
    }

    @Test
    fun `register with valid data returns success with token and user`() {
        val request = RegisterRequest(
            email = "test@example.com",
            username = "testuser",
            password = "password123",
            bio = null
        )

        val result = repository.register(request)

        assertTrue(result.isSuccess, "Expected success but got ${result.exceptionOrNull()}")
        val response = result.getOrNull()
        assertNotNull(response)

        val actual = response!!
        assertTrue(actual.token.isNotEmpty(), "Token should not be empty")
        assertTrue(actual.refreshToken.isNotEmpty(), "Refresh token should not be empty")
        assertEquals("test@example.com", actual.user.email)
        assertEquals("testuser", actual.user.username)
    }

    @Test
    fun `register inserts user row into database`() {
        val request = RegisterRequest(
            email = "row@example.com",
            username = "rowuser",
            password = "password123",
            bio = null
        )

        repository.register(request)

        val rows = transaction { Users.selectAll().toList() }
        assertEquals(1, rows.size)
        assertEquals("row@example.com", rows[0][Users.email])
    }

    @Test
    fun `register with duplicate email returns conflict`() {
        val request = RegisterRequest(
            email = "duplicate@example.com",
            username = "user1",
            password = "password123",
            bio = null
        )
        repository.register(request)

        val secondRequest = request.copy(username = "user2")
        val result = repository.register(secondRequest)

        assertTrue(result.isFailure, "Expected failure but got ${result.getOrNull()}")
        assertTrue(result.exceptionOrNull() is ConflictException)
    }

    @Test
    fun `register with duplicate username returns conflict`() {
        val request = RegisterRequest(
            email = "first@example.com",
            username = "sameuser",
            password = "password123",
            bio = null
        )
        repository.register(request)

        val secondRequest = request.copy(email = "second@example.com")
        val result = repository.register(secondRequest)

        assertTrue(result.isFailure, "Expected failure but got ${result.getOrNull()}")
        assertTrue(result.exceptionOrNull() is ConflictException)
    }

    @Test
    fun `register with invalid email returns validation error`() {
        val request = RegisterRequest(
            email = "not-an-email",
            username = "user",
            password = "password123",
            bio = null
        )

        val result = repository.register(request)

        assertTrue(result.isFailure, "Expected failure but got ${result.getOrNull()}")
        assertTrue(result.exceptionOrNull() is ValidationException)
    }

    @Test
    fun `register with short password returns validation error`() {
        val request = RegisterRequest(
            email = "valid@example.com",
            username = "user",
            password = "abc",
            bio = null
        )

        val result = repository.register(request)

        assertTrue(result.isFailure, "Expected failure but got ${result.getOrNull()}")
        assertTrue(result.exceptionOrNull() is ValidationException)
    }
}