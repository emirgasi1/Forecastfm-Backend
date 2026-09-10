package com.example.auth

import com.example.auth.model.*
import database.table.UserSessions
import database.table.Users
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import user.User
import java.time.LocalDateTime
import kotlin.uuid.Uuid
import java.util.UUID


class AuthRepository(
    private val jwtIssuer: JwtIssuer = JwtIssuer(),
    private val passwordHasher: PasswordHasher = PasswordHasher()
) {
    fun register(request: RegisterRequest): Result<User> {
        return try {
            if (!request.email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
                return Result.failure(ValidationException("Invalid email format"))
            }
            if (request.password.length < 8) {
                return Result.failure(ValidationException("Password must be at least 8 characters"))
            }

            transaction {
                val existingEmail = Users.selectAll().where { Users.email eq request.email }.firstOrNull()
                val existingUsername = Users.selectAll().where { Users.username eq request.username }.firstOrNull()

                if (existingEmail != null) {
                    return@transaction Result.failure(ConflictException("Email already registered"))
                }
                if (existingUsername != null) {
                    return@transaction Result.failure(ConflictException("Username already taken"))
                }

                val userId = UUID.randomUUID().toString()
                val hashedPassword = passwordHasher.hash(request.password)

                Users.insert {
                    it[Users.id] = userId
                    it[Users.email] = request.email
                    it[Users.username] = request.username
                    it[Users.passwordHash] = hashedPassword
                    it[Users.bio] = request.bio
                    it[Users.profileImageUrl] = request.profileImageUrl
                    it[Users.isVerified] = false
                    it[Users.status] = "ACTIVE"
                }

                val user = getUserById(userId)
                Result.success(user!!)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            transaction {
                val userRow = Users.selectAll().where { Users.email eq email }.firstOrNull()
                    ?: return@transaction Result.failure(AuthException("Invalid credentials"))

                if (!passwordHasher.verify(password, userRow[Users.passwordHash])) {
                    return@transaction Result.failure(AuthException("Invalid credentials"))
                }

                if (userRow[Users.status] != "ACTIVE") {
                    return@transaction Result.failure(AuthException("Account is not active"))
                }

                val user = User(
                    id = userRow[Users.id],
                    email = userRow[Users.email],
                    username = userRow[Users.username],
                    passwordHash = userRow[Users.passwordHash],
                    bio = userRow[Users.bio],
                    profileImageUrl = userRow[Users.profileImageUrl],
                    favoriteLocation = userRow[Users.favoriteLocation],
                    isVerified = userRow[Users.isVerified],
                    createdAt = userRow[Users.createdAt].toString(),  // ← Convert to String
                    lastLoginAt = userRow[Users.lastLoginAt]?.toString(),
                    status = userRow[Users.status]
                )

                val token = jwtIssuer.generateToken(user.id)
                val refreshToken = jwtIssuer.generateRefreshToken(user.id)
                val expiresAt = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)

                val sessionId = UUID.randomUUID().toString()
                val userIdStr = UUID.fromString(user.id).toString()

                UserSessions.insert {
                    it[UserSessions.id] = sessionId
                    it[UserSessions.userId] = userIdStr
                    it[UserSessions.token] = token
                    it[UserSessions.refreshToken] = refreshToken
                    it[UserSessions.expiresAt] = LocalDateTime.now().plusDays(7)
                    it[UserSessions.isActive] = true
                }

                Users.update({ Users.id eq userIdStr }) {
                    it[Users.lastLoginAt] = LocalDateTime.now()
                }

                Result.success(
                    AuthResponse(
                        token = token,
                        refreshToken = refreshToken,
                        user = user,
                        expiresAt = expiresAt
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout(token: String): Result<Unit> {
        return try {
            transaction {
                UserSessions.deleteWhere { UserSessions.token eq token }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun refreshToken(refreshToken: String): Result<AuthResponse> {
        return try {
            transaction {
                val session = UserSessions.selectAll()
                    .where { UserSessions.refreshToken eq refreshToken }
                    .andWhere { UserSessions.isActive eq true }
                    .firstOrNull()
                    ?: return@transaction Result.failure(AuthException("Invalid refresh token"))

                val userId = session[UserSessions.userId]
                val user = getUserById(userId)!!

                val newToken = jwtIssuer.generateToken(user.id)
                val newRefreshToken = jwtIssuer.generateRefreshToken(user.id)
                val expiresAt = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)

                UserSessions.update({ UserSessions.id eq session[UserSessions.id] }) {
                    it[UserSessions.token] = newToken
                    it[UserSessions.refreshToken] = newRefreshToken
                    it[UserSessions.expiresAt] = LocalDateTime.now().plusDays(7)
                }

                Result.success(
                    AuthResponse(
                        token = newToken,
                        refreshToken = newRefreshToken,
                        user = user,
                        expiresAt = expiresAt
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun forgotPassword(email: String): Result<Unit> {
        return try {
            transaction {
                val userRow = Users.selectAll().where { Users.email eq email }.firstOrNull()
                if (userRow == null) {
                    return@transaction Result.success(Unit)
                }
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun resetPassword(token: String, newPassword: String): Result<Unit> {
        return try {
            if (newPassword.length < 8) {
                return Result.failure(ValidationException("Password must be at least 8 characters"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getUserById(id: String): User? {
        return Users.selectAll()
            .where { Users.id eq id }
            .singleOrNull()
            ?.let { row ->
                User(
                    id = row[Users.id],
                    email = row[Users.email],
                    username = row[Users.username],
                    passwordHash = row[Users.passwordHash],
                    bio = row[Users.bio],
                    profileImageUrl = row[Users.profileImageUrl],
                    favoriteLocation = row[Users.favoriteLocation],
                    isVerified = row[Users.isVerified],
                    createdAt = row[Users.createdAt]?.toString(),  // ← Convert to String
                    lastLoginAt = row[Users.lastLoginAt]?.toString(),
                    status = row[Users.status]
                )
            }
    }
}

class ValidationException(message: String) : Exception(message)
class ConflictException(message: String) : Exception(message)