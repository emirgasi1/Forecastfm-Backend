package com.example.place

import com.example.database.table.Places
import com.example.database.table.SavedPlaces
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.util.UUID

class SavedPlaceRepository {

    fun savePlace(userId: String, placeId: String) {
        transaction {
            val exists = SavedPlaces
                .selectAll()
                .where {
                    (SavedPlaces.userId eq userId) and
                            (SavedPlaces.placeId eq placeId)
                }
                .count() > 0

            if (!exists) {
                SavedPlaces.insert {
                    it[SavedPlaces.id] = UUID.randomUUID().toString()
                    it[SavedPlaces.userId] = userId
                    it[SavedPlaces.placeId] = placeId
                    it[SavedPlaces.createdAt] = Instant.now()
                }
            }
        }
    }

    fun unsavePlace(userId: String, placeId: String) {
        transaction {
            SavedPlaces.deleteWhere {
                (SavedPlaces.userId eq userId) and (SavedPlaces.placeId eq placeId)
            }
        }
    }

    fun isPlaceSaved(userId: String, placeId: String): Boolean {
        return transaction {
            SavedPlaces
                .selectAll()
                .where {
                    (SavedPlaces.userId eq userId) and
                            (SavedPlaces.placeId eq placeId)
                }
                .any()
        }
    }

    fun getSavedPlaces(userId: String): List<Place> {
        return transaction {
            (SavedPlaces innerJoin Places)
                .selectAll()
                .where { SavedPlaces.userId eq userId }
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
}