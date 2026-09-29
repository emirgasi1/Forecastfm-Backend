package com.example.place

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.database.table.Locations
import com.example.database.table.Places
import database.table.Users
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SavedPlaceRepositoryTest {

    private lateinit var savedPlaceRepository: SavedPlaceRepository
    private val userId = UUID.randomUUID().toString()
    private val placeId = "p-1"

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
        savedPlaceRepository = SavedPlaceRepository()

        transaction {
            Users.insert {
                it[Users.id] = userId
                it[Users.email] = "user@example.com"
                it[Users.username] = "testuser"
                it[Users.passwordHash] = "hash"
                it[Users.isVerified] = true
                it[Users.createdAt] = LocalDateTime.now()
                it[Users.status] = "ACTIVE"
            }
            Locations.insert {
                it[Locations.id] = "loc-1"
                it[Locations.name] = "Center"
                it[Locations.description] = "Desc"
                it[Locations.latitude] = 43.0
                it[Locations.longitude] = 18.0
            }
            Places.insert {
                it[Places.id] = placeId
                it[Places.name] = "Museum"
                it[Places.category] = "Culture"
                it[Places.venueId] = "loc-1"
                it[Places.address] = "Street 1"
                it[Places.latitude] = 43.0
                it[Places.longitude] = 18.0
                it[Places.description] = "Museum desc"
                it[Places.rating] = 4.8
                it[Places.createdAt] = Instant.now()
            }
        }
    }

    @Test
    fun `savePlace and isPlaceSaved work correctly`() {
        assertFalse(savedPlaceRepository.isPlaceSaved(userId, placeId))

        savedPlaceRepository.savePlace(userId, placeId)
        assertTrue(savedPlaceRepository.isPlaceSaved(userId, placeId))

        savedPlaceRepository.savePlace(userId, placeId)
        assertTrue(savedPlaceRepository.isPlaceSaved(userId, placeId))
    }

    @Test
    fun `unsavePlace removes saved relationship`() {
        savedPlaceRepository.savePlace(userId, placeId)
        assertTrue(savedPlaceRepository.isPlaceSaved(userId, placeId))

        savedPlaceRepository.unsavePlace(userId, placeId)
        assertFalse(savedPlaceRepository.isPlaceSaved(userId, placeId))
    }

    @Test
    fun `getSavedPlaces returns full place objects for user`() {
        savedPlaceRepository.savePlace(userId, placeId)

        val saved = savedPlaceRepository.getSavedPlaces(userId)
        assertEquals(1, saved.size)
        assertEquals(placeId, saved[0].id)
        assertEquals("Museum", saved[0].name)
    }
}