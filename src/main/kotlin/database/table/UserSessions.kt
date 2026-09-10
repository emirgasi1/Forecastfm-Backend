package database.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime

object UserSessions : Table("user_sessions") {
    val id = text("id")  // Changed from uuid("id")
    val userId = text("user_id").references(Users.id)  // Changed from uuid("user_id")
    val token = varchar("token", 500)
    val refreshToken = varchar("refresh_token", 500)
    val expiresAt = datetime("expires_at")
    val createdAt = datetime("created_at").default(LocalDateTime.now())
    val userAgent = varchar("user_agent", 255).nullable()
    val ipAddress = varchar("ip_address", 45).nullable()
    val isActive = bool("is_active").default(true)

    override val primaryKey = PrimaryKey(id, name = "PK_user_sessions")

    init {
        uniqueIndex(token)
        uniqueIndex(refreshToken)
    }
}