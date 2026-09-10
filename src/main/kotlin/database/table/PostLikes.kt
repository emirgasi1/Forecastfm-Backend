package com.example.database.table

import database.table.Users
import org.jetbrains.exposed.v1.core.Table

object PostLikes : Table("post_likes") {
    val userId = text("userId").references(Users.id)
    val postId = text("postId").references(Posts.id)

    override val primaryKey = PrimaryKey(userId, postId)
}