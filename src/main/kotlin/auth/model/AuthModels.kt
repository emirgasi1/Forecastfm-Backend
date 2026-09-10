package com.example.auth.model

import kotlinx.serialization.Serializable
import user.User

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    val bio: String? = null,
    val profileImageUrl: String? = null
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val refreshToken: String,
    val user: User,
    val expiresAt: Long
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)

@Serializable
data class LogoutRequest(
    val token: String
)