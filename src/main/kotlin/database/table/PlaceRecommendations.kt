package com.example.database.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object PlaceRecommendations : Table("place_recommendations") {
    val id = text("id")
    val name = text("name")
    val category = text("category")
    val location = text("location")
    val description = text("description")
    val suitableFor = text("suitableFor")  // comma-separated list
    val weatherCondition = text("weatherCondition")
    val ageGroup = text("ageGroup")  // comma-separated list
    val rating = double("rating")
    val imageUrl = text("imageUrl").nullable()
    val createdAt = timestamp("createdAt")
    val placeId = text("place_id").references(Places.id)

    override val primaryKey = PrimaryKey(id)
}