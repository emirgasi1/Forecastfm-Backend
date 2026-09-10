package database.table


import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

object Users : Table("users") {
    val id = text("id")  // Changed from uuid("id")
    val email = varchar("email", 255)
    val username = varchar("username", 100)
    val passwordHash = varchar("password_hash", 255)
    val bio = varchar("bio", 500).nullable()
    val profileImageUrl = varchar("profile_image_url", 500).nullable()
    val favoriteLocation = varchar("favorite_location", 255).nullable()
    val isVerified = bool("is_verified").default(false)
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val lastLoginAt = datetime("last_login_at").nullable()
    val status = varchar("status", 20).default("ACTIVE")

    override val primaryKey = PrimaryKey(id, name = "PK_users")

    init {
        uniqueIndex(email)
        uniqueIndex(username)
    }
}