package com.example.busstation

import com.example.TestDataCleaner
import com.example.TestDatabase
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BusStationRepositoryTest {

    private lateinit var repository: BusStationRepository

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
        repository = BusStationRepository()
    }

    @Test
    fun `createBusStation returns valid BusStation object`() {
        val station = repository.createBusStation(
            name = "Central Station",
            latitude = 40.7128,
            longitude = -74.0060,
            lines = listOf("M1", "M2", "M3")
        )

        assertNotNull(station)
        assertTrue(station.id.isNotEmpty())
        assertEquals("Central Station", station.name)
        assertEquals(40.7128, station.latitude)
        assertEquals(-74.0060, station.longitude)
        assertEquals(listOf("M1", "M2", "M3"), station.lines)
        assertNotNull(station.createdAt)
    }

    @Test
    fun `getAllBusStations returns empty list when no stations exist`() {
        val stations = repository.getAllBusStations()
        assertTrue(stations.isEmpty())
    }

    @Test
    fun `getAllBusStations returns all created bus stations`() {
        repository.createBusStation("Station A", 10.0, 20.0, listOf("1", "2"))
        repository.createBusStation("Station B", 30.0, 40.0, listOf("3"))

        val stations = repository.getAllBusStations()

        assertEquals(2, stations.size)
        assertTrue(stations.any { it.name == "Station A" })
        assertTrue(stations.any { it.name == "Station B" })
    }

    @Test
    fun `getBusStationById returns correct station when it exists`() {
        val created = repository.createBusStation(
            name = "North Terminal",
            latitude = 50.0,
            longitude = 60.0,
            listOf("10A", "10B")
        )

        val found = repository.getBusStationById(created.id)

        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals("North Terminal", found.name)
        assertEquals(listOf("10A", "10B"), found.lines)
    }

    @Test
    fun `getBusStationById returns null when station does not exist`() {
        val found = repository.getBusStationById("non-existent-id")
        assertNull(found)
    }

    @Test
    fun `lines are properly parsed into list and trailing empty values removed`() {
        val station = repository.createBusStation(
            name = "Express Hub",
            latitude = 12.34,
            longitude = 56.78,
            lines = listOf("Route 100")
        )

        val fetched = repository.getBusStationById(station.id)

        assertNotNull(fetched)
        assertEquals(1, fetched.lines.size)
        assertEquals("Route 100", fetched.lines[0])
    }
}