package com.example.placerecommendation

import com.example.database.table.PlaceRecommendations
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.util.UUID

class PlaceRecommendationRepository {

    fun createPlaceRecommendation(
        name: String,
        category: String,
        location: String,
        placeId: String,
        description: String,
        suitableFor: List<String>,
        weatherCondition: String,
        ageGroup: List<String>,
        rating: Double,
        imageUrl: String?
    ): PlaceRecommendation {
        val id = UUID.randomUUID().toString()
        val createdAt = Instant.now().toString()

        transaction {
            PlaceRecommendations.insert {
                it[PlaceRecommendations.id] = id
                it[PlaceRecommendations.name] = name
                it[PlaceRecommendations.category] = category
                it[PlaceRecommendations.location] = location
                it[PlaceRecommendations.placeId] = placeId
                it[PlaceRecommendations.description] = description
                it[PlaceRecommendations.suitableFor] = suitableFor.joinToString(",")
                it[PlaceRecommendations.weatherCondition] = weatherCondition
                it[PlaceRecommendations.ageGroup] = ageGroup.joinToString(",")
                it[PlaceRecommendations.rating] = rating
                it[PlaceRecommendations.imageUrl] = imageUrl
                it[PlaceRecommendations.createdAt] = Instant.now()
            }
        }

        return PlaceRecommendation(
            id = id,
            name = name,
            category = category,
            location = location,
            placeId = placeId,
            description = description,
            suitableFor = suitableFor,
            weatherCondition = weatherCondition,
            ageGroup = ageGroup,
            rating = rating,
            imageUrl = imageUrl,
            createdAt = createdAt
        )
    }

    fun getAllRecommendations(): List<PlaceRecommendation> {
        return transaction {
            PlaceRecommendations
                .selectAll()
                .map { row ->
                    PlaceRecommendation(
                        id = row[PlaceRecommendations.id],
                        name = row[PlaceRecommendations.name],
                        category = row[PlaceRecommendations.category],
                        location = row[PlaceRecommendations.location],
                        placeId = row[PlaceRecommendations.placeId],
                        description = row[PlaceRecommendations.description],
                        suitableFor = row[PlaceRecommendations.suitableFor].split(",").filter { it.isNotEmpty() },
                        weatherCondition = row[PlaceRecommendations.weatherCondition],
                        ageGroup = row[PlaceRecommendations.ageGroup].split(",").filter { it.isNotEmpty() },
                        rating = row[PlaceRecommendations.rating],
                        imageUrl = row[PlaceRecommendations.imageUrl],
                        createdAt = row[PlaceRecommendations.createdAt].toString()
                    )
                }
        }
    }

    fun getRecommendationsByFilters(
        category: String? = null,
        suitableFor: String? = null,
        weatherCondition: String? = null,
        ageGroup: String? = null
    ): List<PlaceRecommendation> {
        return transaction {
            val query = PlaceRecommendations.selectAll()
                .let { query ->
                    if (category != null) {
                        query.where { PlaceRecommendations.category eq category }
                    } else query
                }
                .let { query ->
                    if (suitableFor != null) {
                        query.where { PlaceRecommendations.suitableFor like "%$suitableFor%" }
                    } else query
                }
                .let { query ->
                    if (weatherCondition != null) {
                        query.where { PlaceRecommendations.weatherCondition eq weatherCondition }
                    } else query
                }
                .let { query ->
                    if (ageGroup != null) {
                        query.where { PlaceRecommendations.ageGroup like "%$ageGroup%" }
                    } else query
                }

            query.map { row ->
                PlaceRecommendation(
                    id = row[PlaceRecommendations.id],
                    name = row[PlaceRecommendations.name],
                    category = row[PlaceRecommendations.category],
                    location = row[PlaceRecommendations.location],
                    placeId = row[PlaceRecommendations.placeId],
                    description = row[PlaceRecommendations.description],
                    suitableFor = row[PlaceRecommendations.suitableFor].split(",").filter { it.isNotEmpty() },
                    weatherCondition = row[PlaceRecommendations.weatherCondition],
                    ageGroup = row[PlaceRecommendations.ageGroup].split(",").filter { it.isNotEmpty() },
                    rating = row[PlaceRecommendations.rating],
                    imageUrl = row[PlaceRecommendations.imageUrl],
                    createdAt = row[PlaceRecommendations.createdAt].toString()
                )
            }
        }
    }

    fun getRecommendationById(id: String): PlaceRecommendation? {
        return transaction {
            PlaceRecommendations
                .selectAll()
                .where { PlaceRecommendations.id eq id }
                .singleOrNull()
                ?.let { row ->
                    PlaceRecommendation(
                        id = row[PlaceRecommendations.id],
                        name = row[PlaceRecommendations.name],
                        category = row[PlaceRecommendations.category],
                        location = row[PlaceRecommendations.location],
                        placeId = row[PlaceRecommendations.placeId],
                        description = row[PlaceRecommendations.description],
                        suitableFor = row[PlaceRecommendations.suitableFor].split(",").filter { it.isNotEmpty() },
                        weatherCondition = row[PlaceRecommendations.weatherCondition],
                        ageGroup = row[PlaceRecommendations.ageGroup].split(",").filter { it.isNotEmpty() },
                        rating = row[PlaceRecommendations.rating],
                        imageUrl = row[PlaceRecommendations.imageUrl],
                        createdAt = row[PlaceRecommendations.createdAt].toString()
                    )
                }
        }
    }
}