package com.example.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import java.util.Date

class JwtIssuer {
    private val secretKey = System.getenv("JWT_SECRET") ?: "default-secret-key-change-in-production"
    private val algorithm = Algorithm.HMAC256(secretKey)

    fun generateToken(userId: String): String {
        return JWT.create()
            .withSubject(userId)
            .withClaim("type", "access")
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000))
            .sign(algorithm)
    }

    fun generateRefreshToken(userId: String): String {
        return JWT.create()
            .withSubject(userId)
            .withClaim("type", "refresh")
            .withIssuedAt(Date())
            .withExpiresAt(Date(System.currentTimeMillis() + 604800000))
            .sign(algorithm)
    }

    fun verifyToken(token: String): Result<Map<String, Any>> {
        return try {
            val decoded = JWT.require(algorithm).build().verify(token)
            val claims = mapOf(
                "userId" to decoded.subject,
                "type" to decoded.getClaim("type").asString()
            )
            Result.success(claims)
        } catch (e: JWTVerificationException) {
            Result.failure(AuthException("Invalid token: ${e.message}"))
        }
    }
}

class AuthException(message: String) : Exception(message)