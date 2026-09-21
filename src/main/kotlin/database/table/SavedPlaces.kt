package com.example.database.table

import database.table.Users
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object SavedPlaces : Table("saved_places") {
    val id = text("id")
    val userId = text("userId").references(Users.id)
    val placeId = text("placeId").references(Places.id)
    val createdAt = timestamp("createdAt")

    override val primaryKey = PrimaryKey(id)

    init {
        uniqueIndex(userId, placeId)
    }
}