package com.example.music

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.AuthRepository
import com.example.auth.model.RegisterRequest
import com.example.playlist.PlaylistRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MusicHistoryRepositoryTest {

    private lateinit var musicHistoryRepository: MusicHistoryRepository
    private lateinit var playlistRepository: PlaylistRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var userId: String
    private lateinit var playlistId: String

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        musicHistoryRepository = MusicHistoryRepository()
        playlistRepository = PlaylistRepository()
        authRepository = AuthRepository()

        val registerResult = authRepository.register(
            RegisterRequest(
                email = "listener@example.com",
                username = "listener",
                password = "password123",
                bio = null
            )
        )
        userId = registerResult.getOrNull()!!.user.id

        val playlist = playlistRepository.createPlaylist(
            title = "Chill Vibes",
            genre = "Lo-Fi",
            mood = "Relaxed",
            albumImageUrl = null,
            weather = "Sunny",
            temperature = "24°C",
            location = "Central Park",
            spotifyUrl = null,
            youtubeUrl = null,
            bestFor = listOf("Studying", "Relaxing")
        )
        playlistId = playlist.id
    }

    @Test
    fun `getHistory returns empty list when no history exists for user`() {
        val history = musicHistoryRepository.getHistory(userId)
        assertTrue(history.isEmpty())
    }

    @Test
    fun `addHistory inserts entry and getHistory returns joined playlist data`() {
        musicHistoryRepository.addHistory(userId, playlistId)

        val history = musicHistoryRepository.getHistory(userId)

        assertEquals(1, history.size)
        val entry = history[0]
        assertNotNull(entry.id)
        assertEquals(playlistId, entry.playlistId)
        assertEquals("Chill Vibes", entry.title)
        assertEquals("Sunny", entry.weather)
        assertEquals("24°C", entry.temperature)
        assertEquals("Central Park", entry.location)
        assertNotNull(entry.playedAt)
    }

    @Test
    fun `getHistory only returns entries for specified user`() {
        val otherUserResult = authRepository.register(
            RegisterRequest(
                email = "otherlistener@example.com",
                username = "otherlistener",
                password = "password123",
                bio = null
            )
        )
        val otherUserId = otherUserResult.getOrNull()!!.user.id

        musicHistoryRepository.addHistory(userId, playlistId)
        musicHistoryRepository.addHistory(otherUserId, playlistId)

        val userHistory = musicHistoryRepository.getHistory(userId)
        val otherUserHistory = musicHistoryRepository.getHistory(otherUserId)

        assertEquals(1, userHistory.size)
        assertEquals(1, otherUserHistory.size)
    }

    @Test
    fun `addHistory with non-existent user throws exception`() {
        var threw = false
        try {
            musicHistoryRepository.addHistory("non-existent-user", playlistId)
        } catch (e: Exception) {
            threw = true
        }
        assertTrue(threw, "Expected foreign key constraint violation")
    }

    @Test
    fun `addHistory with non-existent playlist throws exception`() {
        var threw = false
        try {
            musicHistoryRepository.addHistory(userId, "non-existent-playlist")
        } catch (e: Exception) {
            threw = true
        }
        assertTrue(threw, "Expected foreign key constraint violation")
    }
}