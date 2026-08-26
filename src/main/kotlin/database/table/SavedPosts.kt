package com.example.database.table

import org.jetbrains.exposed.v1.core.Table

object SavedPosts : Table("saved_posts") {

    val userId = uuid("userId")
        .references(Users.id)

    val postId = uuid("postId")
        .references(Posts.id)

    override val primaryKey = PrimaryKey(userId, postId)
}