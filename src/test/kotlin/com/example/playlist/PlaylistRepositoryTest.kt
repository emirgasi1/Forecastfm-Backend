package com.example.playlist

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.AuthRepository
import com.example.auth.model.RegisterRequest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlaylistRepositoryTest {

    private lateinit var repository: PlaylistRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var userId: String

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        repository = PlaylistRepository()
        authRepository = AuthRepository()

        val registerResult = authRepository.register(
            RegisterRequest(
                email = "playlistuser@example.com",
                username = "playlistuser",
                password = "password123",
                bio = null
            )
        )
        userId = registerResult.getOrNull()!!.user.id
    }

    @Test
    fun `createPlaylist returns created Playlist`() {
        val playlist = repository.createPlaylist(
            title = "Summer Hits",
            genre = "Pop",
            mood = "Energetic",
            albumImageUrl = "http://example.com/cover.jpg",
            weather = "Sunny",
            temperature = "30°C",
            location = "Miami",
            spotifyUrl = "http://spotify.com/playlist/1",
            youtubeUrl = null,
            bestFor = listOf("Driving", "Party")
        )

        assertNotNull(playlist)
        assertTrue(playlist.id.isNotEmpty())
        assertEquals("Summer Hits", playlist.title)
        assertEquals("Pop", playlist.genre)
        assertEquals("30°C", playlist.temperature)
        assertEquals(listOf("Driving", "Party"), playlist.bestFor)
    }

    @Test
    fun `getPlaylists returns all created playlists as PlaylistResponse`() {
        repository.createPlaylist(
            title = "P1", genre = "Rock", mood = "Hyped",
            albumImageUrl = null, weather = "Clear", temperature = "20°C",
            location = "London", spotifyUrl = null, youtubeUrl = null
        )
        repository.createPlaylist(
            title = "P2", genre = "Jazz", mood = "Calm",
            albumImageUrl = null, weather = "Rainy", temperature = "15°C",
            location = "Paris", spotifyUrl = null, youtubeUrl = null
        )

        val playlists = repository.getPlaylists()

        assertEquals(2, playlists.size)
    }

    @Test
    fun `getPlaylistById returns correct playlist or null`() {
        val created = repository.createPlaylist(
            title = "Target Playlist", genre = "Indie", mood = "Chill",
            albumImageUrl = null, weather = "Cloudy", temperature = "18°C",
            location = "Berlin", spotifyUrl = null, youtubeUrl = null
        )

        val found = repository.getPlaylistById(created.id)
        assertNotNull(found)
        assertEquals("Target Playlist", found.title)

        val notFound = repository.getPlaylistById("non-existent-id")
        assertNull(notFound)
    }

    @Test
    fun `favoritePlaylist and unfavoritePlaylist update favorites list`() {
        val playlist = repository.createPlaylist(
            title = "Fav Playlist", genre = "HipHop", mood = "Upbeat",
            albumImageUrl = null, weather = "Sunny", temperature = "25°C",
            location = "NYC", spotifyUrl = null, youtubeUrl = null
        )

        // 1. Favorite
        repository.favoritePlaylist(userId, playlist.id)

        val favIds = repository.getFavoritePlaylistIds(userId)
        assertEquals(1, favIds.size)
        assertEquals(playlist.id, favIds[0])

        val favs = repository.getFavoritePlaylists(userId)
        assertEquals(1, favs.size)
        assertEquals("Fav Playlist", favs[0].title)

        // 2. Unfavorite
        repository.unfavoritePlaylist(userId, playlist.id)

        val updatedFavs = repository.getFavoritePlaylists(userId)
        assertTrue(updatedFavs.isEmpty())
    }

    @Test
    fun `updatePlaylistImage updates albumImageUrl`() {
        val playlist = repository.createPlaylist(
            title = "Image Update Test", genre = "Ambient", mood = "Focus",
            albumImageUrl = null, weather = "Snowy", temperature = "-2°C",
            location = "Oslo", spotifyUrl = null, youtubeUrl = null
        )

        repository.updatePlaylistImage(playlist.id, "/uploads/playlists/cover.jpg")

        val updated = repository.getPlaylistById(playlist.id)
        assertNotNull(updated)
        assertEquals("/uploads/playlists/cover.jpg", updated.albumImageUrl)
    }
}