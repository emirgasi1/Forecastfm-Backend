package com.example.database.table

import org.jetbrains.exposed.v1.core.Table
import database.table.Users

object SavedPosts : Table("saved_posts") {
    val userId = text("userId").references(Users.id)
    val postId = text("postId").references(Posts.id)

    override val primaryKey = PrimaryKey(userId, postId)
}