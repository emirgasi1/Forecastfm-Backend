package com.example.database.table

import database.table.Comments
import database.table.Users
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object CommentLikes : Table("comment_likes") {
    val id = varchar("id", 100)
    val commentId = varchar("comment_id", 100).references(Comments.id)
    val userId = varchar("user_id", 100).references(Users.id)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(commentId, userId)
    }
}