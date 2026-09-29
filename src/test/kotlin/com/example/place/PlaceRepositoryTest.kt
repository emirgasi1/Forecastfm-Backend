package com.example.place

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.database.table.Locations
import com.example.database.table.Places
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlacesRepositoryTest {

    private lateinit var placesRepository: PlacesRepository

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
        placesRepository = PlacesRepository()

        transaction {
            Locations.insert {
                it[Locations.id] = "loc-sarajevo"
                it[Locations.name] = "Sarajevo Center"
                it[Locations.description] = "Central Sarajevo"
                it[Locations.latitude] = 43.8563
                it[Locations.longitude] = 18.4131
            }
        }
    }

    private fun insertTestPlace(
        id: String,
        name: String,
        category: String,
        venueId: String? = "loc-sarajevo",
        address: String = "Ferhadija 1",
        rating: Double = 4.5
    ) {
        transaction {
            Places.insert {
                it[Places.id] = id
                it[Places.name] = name
                it[Places.category] = category
                it[Places.venueId] = venueId
                it[Places.address] = address
                it[Places.latitude] = 43.8500
                it[Places.longitude] = 18.4200
                it[Places.description] = "Description for $name"
                it[Places.imageUrl] = null
                it[Places.rating] = rating
                it[Places.createdAt] = Instant.now()
            }
        }
    }

    @Test
    fun `getAllPlaces returns empty list when none exist`() {
        val places = placesRepository.getAllPlaces()
        assertTrue(places.isEmpty())
    }

    @Test
    fun `getAllPlaces returns all inserted places`() {
        insertTestPlace("p1", "Brew Imperial Society", "Café")
        insertTestPlace("p2", "Dveri", "Restaurant")

        val places = placesRepository.getAllPlaces()
        assertEquals(2, places.size)
    }

    @Test
    fun `getPlaceById returns correct place or null`() {
        insertTestPlace("p1", "Brew Imperial Society", "Café")

        val found = placesRepository.getPlaceById("p1")
        assertNotNull(found)
        assertEquals("Brew Imperial Society", found.name)

        val missing = placesRepository.getPlaceById("non-existent")
        assertNull(missing)
    }

    @Test
    fun `getPlacesByVenue filters by venueId correctly`() {
        transaction {
            Locations.insert {
                it[Locations.id] = "loc-1"
                it[Locations.name] = "Location 1"
                it[Locations.description] = "Desc"
                it[Locations.latitude] = 43.0
                it[Locations.longitude] = 18.0
            }
            Locations.insert {
                it[Locations.id] = "loc-other"
                it[Locations.name] = "Other Location"
                it[Locations.description] = "Desc"
                it[Locations.latitude] = 44.0
                it[Locations.longitude] = 19.0
            }
        }

        insertTestPlace(id = "p-1", name = "Place 1", category = "Café", venueId = "loc-1")
        insertTestPlace(id = "p-2", name = "Place 2", category = "Bar", venueId = "loc-other")

        val results = placesRepository.getPlacesByVenue("loc-1")
        assertEquals(1, results.size)
        assertEquals("p-1", results[0].id)
    }

    @Test
    fun `countPlacesByVenue returns accurate count`() {
        insertTestPlace("p1", "Place A", "Café", venueId = "loc-sarajevo")
        insertTestPlace("p2", "Place B", "Restaurant", venueId = "loc-sarajevo")

        val count = placesRepository.countPlacesByVenue("loc-sarajevo")
        assertEquals(2, count)
    }

    @Test
    fun `searchPlaces finds by name, category, or address case-insensitively`() {
        insertTestPlace("p1", "Caffe Tito", "Lounge Bar", address = "Zmaja od Bosne")
        insertTestPlace("p2", "Kino Bosna", "Cinema", address = "Alipašina")

        val resultsByName = placesRepository.searchPlaces("tito")
        assertEquals(1, resultsByName.size)
        assertEquals("Caffe Tito", resultsByName[0].name)

        val resultsByCategory = placesRepository.searchPlaces("cinema")
        assertEquals(1, resultsByCategory.size)
        assertEquals("Kino Bosna", resultsByCategory[0].name)

        val resultsByAddress = placesRepository.searchPlaces("zmaja")
        assertEquals(1, resultsByAddress.size)
        assertEquals("Caffe Tito", resultsByAddress[0].name)

        val blankSearch = placesRepository.searchPlaces("")
        assertTrue(blankSearch.isEmpty())
    }
}