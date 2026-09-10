package com.example.routes

import com.example.auth.AuthException
import com.example.auth.AuthRepository
import com.example.auth.ConflictException
import com.example.auth.ValidationException
import com.example.auth.model.ForgotPasswordRequest
import com.example.auth.model.LoginRequest
import com.example.auth.model.RefreshTokenRequest
import com.example.auth.model.RegisterRequest
import com.example.auth.model.ResetPasswordRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.header
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.authRoutes() {
    val authRepository = AuthRepository()

    post("/auth/register") {
        val request = call.receive<RegisterRequest>()
        val result = authRepository.register(request)

        result.fold(
            onSuccess = { user ->
                call.respond(HttpStatusCode.Created, user)
            },
            onFailure = { error ->
                when (error) {
                    is ValidationException -> call.respond(HttpStatusCode.BadRequest, mapOf<String, String>("error" to (error.message ?: "Validation error")))
                    is ConflictException -> call.respond(HttpStatusCode.Conflict, mapOf<String, String>("error" to (error.message ?: "Conflict")))
                    else -> call.respond(HttpStatusCode.InternalServerError, mapOf<String, String>("error" to (error.message ?: "Internal server error")))
                }
            }
        )
    }

    post("/auth/login") {
        val request = call.receive<LoginRequest>()
        val result = authRepository.login(request.email, request.password)

        result.fold(
            onSuccess = { authResponse ->
                call.respond(HttpStatusCode.OK, authResponse)
            },
            onFailure = { error ->
                when (error) {
                    is AuthException -> call.respond(HttpStatusCode.Unauthorized, mapOf<String, String>("error" to (error.message ?: "Invalid credentials")))
                    else -> call.respond(HttpStatusCode.InternalServerError, mapOf<String, String>("error" to (error.message ?: "Internal server error")))
                }
            }
        )
    }

    post("/auth/logout") {
        val token = call.request.header("Authorization")?.removePrefix("Bearer ")
            ?: return@post call.respond(HttpStatusCode.Unauthorized, mapOf<String, String>("error" to "Missing token"))

        val result = authRepository.logout(token)

        result.fold(
            onSuccess = {
                call.respond(HttpStatusCode.OK, mapOf<String, String>("message" to "Logged out successfully"))
            },
            onFailure = { error ->
                call.respond(HttpStatusCode.InternalServerError, mapOf<String, String>("error" to (error.message ?: "Internal server error")))
            }
        )
    }

    post("/auth/refresh") {
        val request = call.receive<RefreshTokenRequest>()
        val result = authRepository.refreshToken(request.refreshToken)

        result.fold(
            onSuccess = { authResponse ->
                call.respond(HttpStatusCode.OK, authResponse)
            },
            onFailure = { error ->
                when (error) {
                    is AuthException -> call.respond(HttpStatusCode.Unauthorized, mapOf<String, String>("error" to (error.message ?: "Invalid refresh token")))
                    else -> call.respond(HttpStatusCode.InternalServerError, mapOf<String, String>("error" to (error.message ?: "Internal server error")))
                }
            }
        )
    }

    post("/auth/forgot-password") {
        val request = call.receive<ForgotPasswordRequest>()
        val result = authRepository.forgotPassword(request.email)

        result.fold(
            onSuccess = {
                call.respond(HttpStatusCode.OK, mapOf<String, String>("message" to "Password reset email sent"))
            },
            onFailure = { error ->
                call.respond(HttpStatusCode.InternalServerError, mapOf<String, String>("error" to (error.message ?: "Internal server error")))
            }
        )
    }

    post("/auth/reset-password") {
        val request = call.receive<ResetPasswordRequest>()
        val result = authRepository.resetPassword(request.token, request.newPassword)

        result.fold(
            onSuccess = {
                call.respond(HttpStatusCode.OK, mapOf<String, String>("message" to "Password reset successfully"))
            },
            onFailure = { error ->
                when (error) {
                    is ValidationException -> call.respond(HttpStatusCode.BadRequest, mapOf<String, String>("error" to (error.message ?: "Validation error")))
                    else -> call.respond(HttpStatusCode.InternalServerError, mapOf<String, String>("error" to (error.message ?: "Internal server error")))
                }
            }
        )
    }
}