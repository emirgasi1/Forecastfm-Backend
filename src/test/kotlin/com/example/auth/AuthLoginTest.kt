package com.example.auth

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.model.RegisterRequest
import database.table.Users
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class AuthLoginTest {

    private lateinit var repository: AuthRepository
    private val validEmail = "login@example.com"
    private val validUsername = "loginuser"
    private val validPassword = "password123"

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
        repository = AuthRepository()
        repository.register(
            RegisterRequest(
                email = validEmail,
                username = validUsername,
                password = validPassword,
                bio = null
            )
        )
    }

    @Test
    fun `login with valid credentials returns success with tokens`() {
        val result = repository.login(validEmail, validPassword)

        assertTrue(result.isSuccess, "Expected success but got ${result.exceptionOrNull()}")
        val response = result.getOrNull()
        assertNotNull(response)
        assertTrue(response.token.isNotEmpty())
        assertTrue(response.refreshToken.isNotEmpty())
        assertEquals(validEmail, response.user.email)
        assertEquals(validUsername, response.user.username)
    }

    @Test
    fun `login with wrong password returns auth exception`() {
        val result = repository.login(validEmail, "wrong-password")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AuthException)
    }

    @Test
    fun `login with non-existent email returns auth exception`() {
        val result = repository.login("nobody@example.com", validPassword)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AuthException)
    }

    @Test
    fun `login updates lastLoginAt`() {
        val before = transaction {
            Users.selectAll().where { Users.email eq validEmail }.singleOrNull()?.get(Users.lastLoginAt)
        }

        repository.login(validEmail, validPassword)

        val after = transaction {
            Users.selectAll().where { Users.email eq validEmail }.singleOrNull()?.get(Users.lastLoginAt)
        }

        assertTrue(after != null, "lastLoginAt should be set after login")
        assertTrue(
            before == null || !after!!.isBefore(before),
            "lastLoginAt should not go backwards"
        )
    }

    @Test
    fun `refresh with valid token returns new tokens`() {
        val loginResult = repository.login(validEmail, validPassword)
        assertTrue(loginResult.isSuccess, "Login failed: ${loginResult.exceptionOrNull()}")
        val originalRefresh = loginResult.getOrNull()!!.refreshToken

        val refreshResult = repository.refreshToken(originalRefresh)

        assertTrue(refreshResult.isSuccess)
        val newResponse = refreshResult.getOrNull()
        assertNotNull(newResponse)
        assertTrue(newResponse.token.isNotEmpty())
        assertTrue(newResponse.refreshToken.isNotEmpty())
    }

    @Test
    fun `refresh with invalid token returns auth exception`() {
        val result = repository.refreshToken("this-is-not-a-valid-token")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is AuthException)
    }

    @Test
    fun `logout removes the session`() {
        val loginResult = repository.login(validEmail, validPassword)
        val token = loginResult.getOrNull()!!.token

        val logoutResult = repository.logout(token)

        assertTrue(logoutResult.isSuccess)

        val refreshResult = repository.refreshToken(loginResult.getOrNull()!!.refreshToken)
        assertTrue(refreshResult.isFailure, "Refresh should fail after logout")
    }

    @Test
    fun `forgot password with valid email returns success`() {
        val result = repository.forgotPassword(validEmail)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `forgot password with unknown email returns success`() {
        val result = repository.forgotPassword("nobody@example.com")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `reset password with short password returns validation error`() {
        val result = repository.resetPassword("any-token", "abc")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is ValidationException)
    }
}