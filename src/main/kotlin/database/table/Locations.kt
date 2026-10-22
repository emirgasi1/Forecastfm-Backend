package com.example.database.table

import org.jetbrains.exposed.v1.core.Table

object Locations : Table("locations") {
    val id = text("id")
    val name = varchar("name", 100).uniqueIndex()
    val description = text("description")
    val latitude = double("latitude")
    val longitude = double("longitude")
    val category = text("category").default("general")
    val address = text("address").nullable()
    val openingHours = text("opening_hours").nullable()
    val outdoorSeating = bool("outdoor_seating").default(false)

    override val primaryKey = PrimaryKey(id)
}