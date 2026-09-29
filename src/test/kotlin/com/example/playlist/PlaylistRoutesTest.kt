package com.example.playlist

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.AuthRepository
import com.example.auth.model.RegisterRequest
import com.example.routes.playlistRoutes
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import playlist.Playlist
import playlist.UpdatePlaylistImageRequest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PlaylistRoutesTest {

    private lateinit var userId: String

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        val authRepository = AuthRepository()
        val registerResult = authRepository.register(
            RegisterRequest(
                email = "playlistrouteuser@example.com",
                username = "playlistrouteuser",
                password = "password123",
                bio = null
            )
        )
        userId = registerResult.getOrNull()!!.user.id
    }

    @Test
    fun `POST api playlists creates a playlist and returns 201 Created`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { playlistRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val request = CreatePlaylistRequest(
            title = "Chill Beats",
            genre = "Lo-Fi",
            mood = "Relaxed",
            albumImageUrl = null,
            weather = "Sunny",
            temperature = "22°C",
            location = "Tokyo",
            spotifyUrl = null,
            youtubeUrl = null
        )

        val response = client.post("/api/playlists") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val created: Playlist = response.body()
        assertNotNull(created.id)
        assertEquals("Chill Beats", created.title)
    }

    @Test
    fun `GET api playlists returns all playlists`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { playlistRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val repo = PlaylistRepository()
        repo.createPlaylist("P1", "Pop", "Happy", null, "Sunny", "25°C", "LA", null, null)
        repo.createPlaylist("P2", "Rock", "Energetic", null, "Cloudy", "15°C", "NY", null, null)

        val response = client.get("/api/playlists")

        assertEquals(HttpStatusCode.OK, response.status)
        val playlists: List<PlaylistResponse> = response.body()
        assertEquals(2, playlists.size)
    }

    @Test
    fun `GET api playlists id returns 200 OK when playlist exists`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { playlistRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val repo = PlaylistRepository()
        val playlist = repo.createPlaylist("Target Playlist", "Jazz", "Calm", null, "Rainy", "18°C", "Paris", null, null)

        val response = client.get("/api/playlists/${playlist.id}")

        assertEquals(HttpStatusCode.OK, response.status)
        val fetched: PlaylistResponse = response.body()
        assertEquals(playlist.id, fetched.id)
        assertEquals("Target Playlist", fetched.title)
    }

    @Test
    fun `GET api playlists id returns 404 NotFound when playlist does not exist`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { playlistRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/playlists/non-existent-id")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST, GET, and DELETE favorite playlist endpoints work as expected`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { playlistRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val repo = PlaylistRepository()
        val playlist = repo.createPlaylist("Fav Test", "Indie", "Chill", null, "Clear", "20°C", "Berlin", null, null)

        // 1. Favorite
        val favResponse = client.post("/api/playlists/${playlist.id}/favorite") {
            header("User-Id", userId)
        }
        assertEquals(HttpStatusCode.OK, favResponse.status)

        // 2. Get Favorite IDs
        val favIdsResponse = client.get("/api/playlists/favorites") {
            header("User-Id", userId)
        }
        assertEquals(HttpStatusCode.OK, favIdsResponse.status)
        val favIds: List<String> = favIdsResponse.body()
        assertEquals(1, favIds.size)
        assertEquals(playlist.id, favIds[0])

        // 3. Get Saved Playlists (Full object response)
        val savedPlaylistsResponse = client.get("/api/playlists/saved") {
            header("User-Id", userId)
        }
        assertEquals(HttpStatusCode.OK, savedPlaylistsResponse.status)
        val savedPlaylists: List<PlaylistResponse> = savedPlaylistsResponse.body()
        assertEquals(1, savedPlaylists.size)
        assertEquals("Fav Test", savedPlaylists[0].title)

        // 4. Unfavorite
        val unfavResponse = client.delete("/api/playlists/${playlist.id}/favorite") {
            header("User-Id", userId)
        }
        assertEquals(HttpStatusCode.OK, unfavResponse.status)

        // 5. Verify empty
        val updatedFavIdsResponse = client.get("/api/playlists/favorites") {
            header("User-Id", userId)
        }
        val updatedFavIds: List<String> = updatedFavIdsResponse.body()
        assertEquals(0, updatedFavIds.size)
    }

    @Test
    fun `PUT api playlists playlistId image updates playlist album image`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { playlistRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val repo = PlaylistRepository()
        val playlist = repo.createPlaylist("Image Test", "EDM", "Hype", null, "Sunny", "28°C", "Ibiza", null, null)

        val request = UpdatePlaylistImageRequest(imageUrl = "/uploads/playlists/new_cover.png")

        val response = client.put("/api/playlists/${playlist.id}/image") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.OK, response.status)

        val updatedPlaylist = repo.getPlaylistById(playlist.id)
        assertNotNull(updatedPlaylist)
        assertEquals("/uploads/playlists/new_cover.png", updatedPlaylist.albumImageUrl)
    }

    @Test
    fun `GET api playlists favorites returns 400 BadRequest when User-Id header is missing`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { playlistRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/playlists/favorites")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}