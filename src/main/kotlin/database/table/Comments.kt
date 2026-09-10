package database.table

import com.example.database.table.Posts
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object Comments: Table("comments") {
    val id = text("id")
    val userId = text("userId").references(Users.id)
    val postId = text("postId").references(Posts.id)
    val text = text("text")
    val createdAt = timestamp("createdAt")
    val likes = integer("likes").default(0)

    override val primaryKey = PrimaryKey(id)
}