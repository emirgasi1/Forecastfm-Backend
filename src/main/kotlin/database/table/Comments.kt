package com.example.database.table

import com.example.database.table.Comments.primaryKey
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp
import kotlin.uuid.Uuid

object Comments: Table("comments") {
    val id= uuid("id")
    val userId= uuid("userId").references(Users.id)
    val postId=uuid("postId").references(Posts.id)
    val text=text("text")
    val createdAt=timestamp("createdAt")
    val likes=integer("likes").default(0)

    override val primaryKey=PrimaryKey(id)
}