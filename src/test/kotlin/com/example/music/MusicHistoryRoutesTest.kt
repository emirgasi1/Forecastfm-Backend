package com.example.music

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.AuthRepository
import com.example.auth.model.RegisterRequest
import com.example.music.musichistory.AddMusicHistoryRequest
import com.example.music.musichistory.MusicHistoryEntry
import com.example.playlist.PlaylistRepository
import com.example.routes.musicHistoryRoutes
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MusicHistoryRoutesTest {

    private lateinit var userId: String
    private lateinit var playlistId: String

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        val authRepository = AuthRepository()
        val registerResult = authRepository.register(
            RegisterRequest(
                email = "routehistoryuser@example.com",
                username = "routehistoryuser",
                password = "password123",
                bio = null
            )
        )
        userId = registerResult.getOrNull()!!.user.id

        val playlistRepository = PlaylistRepository()
        val playlist = playlistRepository.createPlaylist(
            title = "Rainy Day Tunes",
            genre = "Acoustic",
            mood = "Melancholy",
            albumImageUrl = null,
            weather = "Rainy",
            temperature = "18°C",
            location = "Seattle",
            spotifyUrl = null,
            youtubeUrl = null
        )
        playlistId = playlist.id
    }

    @Test
    fun `POST api music-history adds entry and returns 201 Created`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { musicHistoryRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val request = AddMusicHistoryRequest(
            userId = userId,
            playlistId = playlistId
        )

        val response = client.post("/api/music-history") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.Created, response.status)
    }

    @Test
    fun `GET api music-history userId returns user history list`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { musicHistoryRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val repository = MusicHistoryRepository()
        repository.addHistory(userId, playlistId)

        val response = client.get("/api/music-history/$userId")

        assertEquals(HttpStatusCode.OK, response.status)
        val history: List<MusicHistoryEntry> = response.body()
        assertEquals(1, history.size)
        assertEquals("Rainy Day Tunes", history[0].title)
    }

    @Test
    fun `GET api music-history userId returns empty list when no history exists`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { musicHistoryRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/music-history/$userId")

        assertEquals(HttpStatusCode.OK, response.status)
        val history: List<MusicHistoryEntry> = response.body()
        assertEquals(0, history.size)
    }
}