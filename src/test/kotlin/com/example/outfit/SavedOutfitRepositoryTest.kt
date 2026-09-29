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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SavedOutfitRepositoryTest {

    private lateinit var savedOutfitRepository: SavedOutfitRepository
    private lateinit var outfitRepository: OutfitRepository

    @BeforeEach
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        savedOutfitRepository = SavedOutfitRepository()
        outfitRepository = OutfitRepository()

        transaction {
            Outfits.deleteAll()
            Users.deleteAll()

            // Seed user records for testing
            listOf("user_123", "user_456").forEach { userId ->
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
    fun `saveOutfit persists saved relationship between user and outfit`() {
        val outfit = outfitRepository.createOutfit("user_123", "img1", "Casual Wear", "Sunny", "Spring")

        // Executed directly since saveOutfit returns Unit
        savedOutfitRepository.saveOutfit("user_123", outfit.id)

        val isSaved = savedOutfitRepository.isOutfitSaved("user_123", outfit.id)
        assertTrue(isSaved)
    }

    @Test
    fun `unsaveOutfit removes relationship successfully`() {
        val outfit = outfitRepository.createOutfit("user_123", "img1", "Formal Suit", "Cool", "Fall")
        savedOutfitRepository.saveOutfit("user_123", outfit.id)

        // Executed directly since unsaveOutfit returns Unit
        savedOutfitRepository.unsaveOutfit("user_123", outfit.id)

        val isSaved = savedOutfitRepository.isOutfitSaved("user_123", outfit.id)
        assertFalse(isSaved)
    }

    @Test
    fun `isOutfitSaved returns false when outfit is not saved by user`() {
        val outfit = outfitRepository.createOutfit("user_123", "img1", "Beachwear", "Hot", "Summer")

        val isSaved = savedOutfitRepository.isOutfitSaved("user_123", outfit.id)
        assertFalse(isSaved)
    }

    @Test
    fun `getSavedOutfits returns only outfits saved by specified user`() {
        val outfit1 = outfitRepository.createOutfit("user_123", "img1", "Outfit 1", "Sunny", "Summer")
        val outfit2 = outfitRepository.createOutfit("user_456", "img2", "Outfit 2", "Rainy", "Winter")

        savedOutfitRepository.saveOutfit("user_123", outfit1.id)
        savedOutfitRepository.saveOutfit("user_456", outfit2.id)

        val savedForUser1: List<Outfit> = savedOutfitRepository.getSavedOutfits("user_123")

        assertEquals(1, savedForUser1.size)
        assertEquals(outfit1.id, savedForUser1[0].id)
        assertEquals("Outfit 1", savedForUser1[0].title)
    }
}