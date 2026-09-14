package com.example.database.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object BusStations : Table("bus_stations") {
    val id = text("id")
    val name = text("name")
    val latitude = double("latitude")
    val longitude = double("longitude")
    val lines = text("lines")
    val createdAt = timestamp("createdAt")

    override val primaryKey = PrimaryKey(id)
}