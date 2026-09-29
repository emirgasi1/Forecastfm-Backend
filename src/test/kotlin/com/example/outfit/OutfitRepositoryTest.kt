package com.example.outfit

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.database.table.Outfits
import database.table.Users
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OutfitRepositoryTest {

    private lateinit var repository: OutfitRepository

    @BeforeEach
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
        repository = OutfitRepository()

        transaction {
            // Clear tables in foreign key order
            Outfits.deleteAll()
            Users.deleteAll()

            // Seed primary test users required by foreign key constraints
            listOf("u1", "u2", "user_123").forEach { userId ->
                Users.insert { row ->
                    row[id] = userId
                    row[email] = "$userId@example.com"
                    row[username] = "user_$userId"
                    row[passwordHash] = "hashed_pass"
                }
            }
        }
    }

    @Test
    fun `createOutfit creates and persists outfit successfully`() {
        val outfit = repository.createOutfit(
            userId = "u1",
            imageUrl = "https://example.com/jacket.jpg",
            title = "Puffer Jacket",
            weatherCondition = "Snowy",
            season = "Winter",
            storeName = "Zara",
            price = "$120.00"
        )

        assertNotNull(outfit.id)
        assertEquals("u1", outfit.userId)
        assertEquals("Puffer Jacket", outfit.title)
        assertEquals("Zara", outfit.storeName)

        val retrieved = repository.getOutfitById(outfit.id)
        assertNotNull(retrieved)
        assertEquals(outfit.id, retrieved.id)
        assertEquals("Puffer Jacket", retrieved.title)
    }

    @Test
    fun `getOutfitById returns null for non-existent id`() {
        val result = repository.getOutfitById("non_existent_id")
        assertNull(result)
    }

    @Test
    fun `getTrendingOutfits returns outfits ordered by likes descending`() {
        val o1 = repository.createOutfit("u1", "img1", "Outfit 1", "Sunny", "Summer")
        val o2 = repository.createOutfit("u2", "img2", "Outfit 2", "Sunny", "Summer")

        repository.likeOutfit(o2.id)

        val trending = repository.getTrendingOutfits()
        assertEquals(2, trending.size)
        assertEquals(o2.id, trending[0].id)
        assertEquals(1, trending[0].likes)
        assertEquals(o1.id, trending[1].id)
        assertEquals(0, trending[1].likes)
    }

    @Test
    fun `getOutfitsByWeather filters outfits correctly`() {
        repository.createOutfit("u1", "img1", "Raincoat", "Rainy", "Spring")
        repository.createOutfit("u2", "img2", "Sunglasses", "Sunny", "Summer")

        val rainyOutfits = repository.getOutfitsByWeather("Rainy")
        assertEquals(1, rainyOutfits.size)
        assertEquals("Raincoat", rainyOutfits[0].title)
    }

    @Test
    fun `likeOutfit increments like count`() {
        val outfit = repository.createOutfit("u1", "img1", "Coat", "Cold", "Winter")
        assertEquals(0, outfit.likes)

        // Calling directly without capturing return type if likeOutfit returns Unit
        repository.likeOutfit(outfit.id)

        val updatedOutfit = repository.getOutfitById(outfit.id)
        assertNotNull(updatedOutfit)
        assertEquals(1, updatedOutfit.likes)
    }
}