package com.example.user

import database.table.Users
import database.table.Users.lastLoginAt
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import user.User
import java.time.LocalDateTime
import java.util.UUID

class UserRepository {
    fun createUser(
        email: String,
        username: String,
        passwordHash: String,
        bio: String? = null,
        profileImageUrl: String? = null,
        favoriteLocation: String? = null
    ): String {
        val id = UUID.randomUUID().toString()
        transaction {
            Users.insert {
                it[Users.id] = id
                it[Users.email] = email
                it[Users.username] = username
                it[Users.passwordHash] = passwordHash
                it[Users.bio] = bio
                it[Users.profileImageUrl] = profileImageUrl
                it[Users.favoriteLocation] = favoriteLocation
                it[Users.isVerified] = false
                it[Users.createdAt] = LocalDateTime.now()
                it[Users.status] = "ACTIVE"
            }
        }
        return id
    }

    fun getUserById(id: String): User? {
        return transaction {
            Users
                .selectAll()
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
                        lastLoginAt = row[Users.lastLoginAt]?.toString(),  // ← Convert to String
                        status = row[Users.status]
                    )
                }
        }
    }

    fun getUserByEmail(email: String): User? {
        return transaction {
            Users
                .selectAll()
                .where { Users.email eq email }
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
                        lastLoginAt = row[Users.lastLoginAt]?.toString(),  // ← Convert to String
                        status = row[Users.status]
                    )
                }
        }
    }

    fun getUserByUsername(username: String): User? {
        return transaction {
            Users
                .selectAll()
                .where { Users.username eq username }
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
                        lastLoginAt = row[Users.lastLoginAt]?.toString(),  // ← Convert to String
                        status = row[Users.status]
                    )
                }
        }
    }

    fun updateLastLogin(userId: String) {
        transaction {
            Users.update({ Users.id eq userId }) {
                it[lastLoginAt] = LocalDateTime.now()
            }
        }
    }

    fun updateProfile(
        userId: String,
        username: String,
        bio: String?,
        favoriteLocation: String?
    ): Boolean {
        return transaction {
            val updated = Users.update({ Users.id eq userId }) {
                it[Users.username] = username
                it[Users.bio] = bio
                it[Users.favoriteLocation] = favoriteLocation
            }
            updated > 0
        }
    }

    fun updateProfileImage(userId: String, imageUrl: String): Boolean {
        return transaction {
            val updated = Users.update({ Users.id eq userId }) {
                it[Users.profileImageUrl] = imageUrl
            }
            updated > 0
        }
    }
}