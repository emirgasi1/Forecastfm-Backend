package com.example.place

import com.example.database.table.Places
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.util.UUID

class PlacesRepository {

    fun getPlacesByVenue(venueId: String): List<Place> {
        return transaction {
            Places
                .selectAll()
                .where { Places.venueId eq venueId }
                .map { row ->
                    Place(
                        id = row[Places.id],
                        name = row[Places.name],
                        category = row[Places.category],
                        venueId = row[Places.venueId],
                        address = row[Places.address],
                        latitude = row[Places.latitude],
                        longitude = row[Places.longitude],
                        description = row[Places.description],
                        imageUrl = row[Places.imageUrl],
                        rating = row[Places.rating],
                        createdAt = row[Places.createdAt].toString()
                    )
                }
        }
    }

    fun getPlaceById(id: String): Place? {
        return transaction {
            Places
                .selectAll()
                .where { Places.id eq id }
                .singleOrNull()
                ?.let { row ->
                    Place(
                        id = row[Places.id],
                        name = row[Places.name],
                        category = row[Places.category],
                        venueId = row[Places.venueId],
                        address = row[Places.address],
                        latitude = row[Places.latitude],
                        longitude = row[Places.longitude],
                        description = row[Places.description],
                        imageUrl = row[Places.imageUrl],
                        rating = row[Places.rating],
                        createdAt = row[Places.createdAt].toString()
                    )
                }
        }
    }
}