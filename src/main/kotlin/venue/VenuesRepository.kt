package com.example.venue

import com.example.database.table.Venues
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.like
import org.jetbrains.exposed.v1.core.lowerCase
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant

class VenuesRepository {

    fun getAllVenues(): List<Venue> = transaction {
        Venues.selectAll().map { row -> row.toVenue() }
    }

    fun getVenueById(id: String): Venue? = transaction {
        Venues.selectAll().where { Venues.id eq id }.singleOrNull()?.toVenue()
    }

    fun getVenuesByLocation(locationId: String): List<Venue> = transaction {
        Venues.selectAll().where { Venues.locationId eq locationId }.map { it.toVenue() }
    }

    fun searchVenues(query: String): List<Venue> {
        if (query.isBlank()) return emptyList()
        val pattern = "%${query.lowercase()}%"
        return transaction {
            Venues.selectAll()
                .where {
                    (Venues.name.lowerCase() like pattern) or
                            (Venues.category.lowerCase() like pattern)
                }
                .map { it.toVenue() }
        }
    }

    fun bulkInsert(venues: List<Venue>) = transaction {
        for (v in venues) {
            Venues.insert {
                it[id] = v.id
                it[name] = v.name
                it[category] = v.category
                it[locationId] = v.locationId
                it[address] = v.address
                it[latitude] = v.latitude
                it[longitude] = v.longitude
                it[description] = v.description
                it[imageUrl] = v.imageUrl
                it[createdAt] = Instant.now()
            }
        }
    }

    private fun org.jetbrains.exposed.v1.core.ResultRow.toVenue() = Venue(
        id = this[Venues.id],
        name = this[Venues.name],
        category = this[Venues.category],
        locationId = this[Venues.locationId],
        address = this[Venues.address],
        latitude = this[Venues.latitude],
        longitude = this[Venues.longitude],
        description = this[Venues.description],
        imageUrl = this[Venues.imageUrl],
        createdAt = this[Venues.createdAt].toString()
    )
}