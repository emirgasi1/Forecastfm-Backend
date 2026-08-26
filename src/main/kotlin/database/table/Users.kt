package com.example.database.table

import org.jetbrains.exposed.v1.core.Table

object Users : Table("users") {

    val id = uuid("id")
    val username = varchar("username", 50)
    val bio = text("bio").nullable()
    val profileImageUrl = text("profileImageUrl").nullable()
    val favoriteLocation = text("favoriteLocation").nullable()

    override val primaryKey= PrimaryKey(id)
}