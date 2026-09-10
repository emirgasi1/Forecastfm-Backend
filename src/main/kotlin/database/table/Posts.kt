package com.example.database.table

import database.table.Users
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object Posts : Table("posts") {
    val id = text("id")
    val userId = text("userId").references(Users.id)
    val caption = text("caption").nullable()
    val imageUrl = text("imageUrl").nullable()
    val createdAt = timestamp("createdAt")

    override val primaryKey = PrimaryKey(id)
}