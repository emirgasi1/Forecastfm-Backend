package com.example.database.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object Places : Table("places") {
    val id = text("id")
    val name = text("name")
    val category = text("category")
    val venueId = text("venueId").references(Locations.id)
    val address = text("address")
    val latitude = double("latitude")
    val longitude = double("longitude")
    val description = text("description")
    val imageUrl = text("imageUrl").nullable()
    val rating = double("rating").default(0.0)
    val createdAt = timestamp("createdAt")

    override val primaryKey = PrimaryKey(id)
}