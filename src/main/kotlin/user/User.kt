package user

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class User(
    val id: String,
    val email: String,
    val username: String,
    val passwordHash: String,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val favoriteLocation: String? = null,
    val isVerified: Boolean = false,
    val createdAt: String? = null,  // ← Changed to String
    val lastLoginAt: String? = null,
    val status: String = "ACTIVE"
)