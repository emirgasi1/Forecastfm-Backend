package com.example.database.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object Venues : Table("venues") {
    val id = text("id")                                    // e.g. "node-123456789"
    val name = varchar("name", 200)
    val category = text("category")                        // "cafe", "restaurant", "bus_stop", ...
    val locationId = text("locationId").references(Locations.id).nullable()  // neighborhood FK
    val address = text("address").nullable()
    val openingHours = text("openingHours").nullable()
    val latitude = double("latitude")
    val longitude = double("longitude")
    val description = text("description").nullable()
    val imageUrl = text("imageUrl").nullable()
    val createdAt = timestamp("createdAt")

    override val primaryKey = PrimaryKey(id)
}