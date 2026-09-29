package com.example.location

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.database.table.Locations
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocationRepositoryTest {

    private lateinit var locationRepository: LocationRepository

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        locationRepository = LocationRepository()
    }

    private fun insertTestLocation(
        id: String,
        name: String,
        description: String,
        latitude: Double,
        longitude: Double
    ) {
        transaction {
            Locations.insert {
                it[Locations.id] = id
                it[Locations.name] = name
                it[Locations.description] = description
                it[Locations.latitude] = latitude
                it[Locations.longitude] = longitude
            }
        }
    }

    @Test
    fun `getLocations returns empty list when no locations exist`() {
        val locations = locationRepository.getLocations()

        assertTrue(locations.isEmpty())
    }

    @Test
    fun `getLocations returns all inserted locations`() {
        insertTestLocation(
            id = "loc-1",
            name = "Bascarsija",
            description = "Historic old bazaar in Sarajevo",
            latitude = 43.8598,
            longitude = 18.4313
        )
        insertTestLocation(
            id = "loc-2",
            name = "Vjecnica",
            description = "Sarajevo City Hall",
            latitude = 43.8591,
            longitude = 18.4334
        )

        val locations = locationRepository.getLocations()

        assertEquals(2, locations.size)

        val bascarsija = locations.first { it.id == "loc-1" }
        assertEquals("Bascarsija", bascarsija.name)
        assertEquals("Historic old bazaar in Sarajevo", bascarsija.description)
        assertEquals(43.8598, bascarsija.latitude)
        assertEquals(18.4313, bascarsija.longitude)

        val vjecnica = locations.first { it.id == "loc-2" }
        assertEquals("Vjecnica", vjecnica.name)
        assertEquals("Sarajevo City Hall", vjecnica.description)
    }

    @Test
    fun `getLocations maps fields accurately from database`() {
        insertTestLocation(
            id = "loc-3",
            name = "Trebevic Cable Car",
            description = "Panoramic cable car to Mount Trebevic",
            latitude = 43.8560,
            longitude = 18.4350
        )

        val locations = locationRepository.getLocations()

        assertEquals(1, locations.size)
        val location = locations.first()
        assertEquals("loc-3", location.id)
        assertEquals("Trebevic Cable Car", location.name)
        assertEquals("Panoramic cable car to Mount Trebevic", location.description)
        assertEquals(43.8560, location.latitude)
        assertEquals(18.4350, location.longitude)
    }
}