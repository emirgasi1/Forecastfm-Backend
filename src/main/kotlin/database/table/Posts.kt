package com.example.database.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object Posts : Table("posts") {

    val id = uuid("id")

    val userId = uuid("userId").references(Users.id)

    val caption = text("caption").nullable()
    val imageUrl = text("imageUrl").nullable()
    val createdAt = timestamp("createdAt")

    override val primaryKey = PrimaryKey(id)
}