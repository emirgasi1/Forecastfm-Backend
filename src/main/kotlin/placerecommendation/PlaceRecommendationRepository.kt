package com.example.placerecommendation

import com.example.database.table.PlaceRecommendations
import com.example.database.table.Places
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
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
    ): PlaceRecommendation = transaction {
        val newId = UUID.randomUUID().toString()
        val suitableForString = suitableFor.joinToString(",")
        val ageGroupString = ageGroup.joinToString(",")
        val now = Instant.now()

        PlaceRecommendations.insert {
            it[PlaceRecommendations.id] = newId
            it[PlaceRecommendations.name] = name
            it[PlaceRecommendations.category] = category
            it[PlaceRecommendations.location] = location
            it[PlaceRecommendations.description] = description
            it[PlaceRecommendations.suitableFor] = suitableForString
            it[PlaceRecommendations.weatherCondition] = weatherCondition
            it[PlaceRecommendations.ageGroup] = ageGroupString
            it[PlaceRecommendations.rating] = rating
            it[PlaceRecommendations.imageUrl] = imageUrl
            it[PlaceRecommendations.createdAt] = now
            it[PlaceRecommendations.placeId] = placeId
        }

        PlaceRecommendation(
            id = newId,
            name = name,
            category = category,
            location = location,
            description = description,
            suitableFor = suitableFor,
            weatherCondition = weatherCondition,
            ageGroup = ageGroup,
            rating = rating,
            imageUrl = imageUrl,
            createdAt = now.toString(),
            placeId = placeId
        )
    }

    fun getRecommendationById(id: String): PlaceRecommendation? = transaction {
        PlaceRecommendations
            .selectAll()
            .andWhere { PlaceRecommendations.id eq id }
            .map { it.toPlaceRecommendation() }
            .singleOrNull()
    }

    fun getAllRecommendations(): List<PlaceRecommendation> = transaction {
        PlaceRecommendations
            .selectAll()
            .map { it.toPlaceRecommendation() }
    }

    fun getRecommendationsByFilters(
        category: String? = null,
        weatherCondition: String? = null,
        suitableFor: String? = null,
        ageGroup: String? = null
    ): List<PlaceRecommendation> = transaction {
        var query = PlaceRecommendations.selectAll()

        category?.let { cat ->
            query = query.andWhere { PlaceRecommendations.category eq cat }
        }
        weatherCondition?.let { weather ->
            query = query.andWhere { PlaceRecommendations.weatherCondition eq weather }
        }
        suitableFor?.let { suitable ->
            query = query.andWhere { PlaceRecommendations.suitableFor like "%$suitable%" }
        }
        ageGroup?.let { age ->
            query = query.andWhere { PlaceRecommendations.ageGroup like "%$age%" }
        }

        query.map { it.toPlaceRecommendation() }
    }

    private fun ResultRow.toPlaceRecommendation(): PlaceRecommendation {
        return PlaceRecommendation(
            id = this[PlaceRecommendations.id],
            name = this[PlaceRecommendations.name],
            category = this[PlaceRecommendations.category],
            location = this[PlaceRecommendations.location],
            description = this[PlaceRecommendations.description],
            suitableFor = this[PlaceRecommendations.suitableFor].split(",").filter { it.isNotBlank() },
            weatherCondition = this[PlaceRecommendations.weatherCondition],
            ageGroup = this[PlaceRecommendations.ageGroup].split(",").filter { it.isNotBlank() },
            rating = this[PlaceRecommendations.rating],
            imageUrl = this[PlaceRecommendations.imageUrl],
            createdAt = this[PlaceRecommendations.createdAt].toString(),
            placeId = this[PlaceRecommendations.placeId]
        )
    }
}