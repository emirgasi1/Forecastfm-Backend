package com.example.music

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.routes.musicRoutes
import com.example.youtube.YouTubeEnrichedItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MusicRoutesTest {

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
        System.setProperty("YOUTUBE_API_KEY", "test-api-key")
    }

    private fun createMockHttpClient(): HttpClient {
        val mockEngine = MockEngine { request: HttpRequestData ->
            val url = request.url.toString()
            when {
                url.contains("playlistItems") -> {
                    respond(
                        content = """
                        {
                          "items": [
                            {
                              "snippet": {
                                "title": "Song Title",
                                "videoOwnerChannelTitle": "Artist Name",
                                "resourceId": { "videoId": "vid123" },
                                "thumbnails": {
                                  "medium": { "url": "https://img.youtube.com/vi/vid123/medium.jpg" }
                                }
                              }
                            }
                          ]
                        }
                    """.trimIndent(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
                url.contains("videos") -> {
                    respond(
                        content = """
                        {
                          "items": [
                            {
                              "id": "vid123",
                              "contentDetails": {
                                "duration": "PT3M45S"
                              }
                            }
                          ]
                        }
                    """.trimIndent(),
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                    )
                }
                else -> respond("Not Found", HttpStatusCode.NotFound)
            }
        }

        return HttpClient(mockEngine) {
            install(ClientContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }

    @Test
    fun `POST api music creates track and returns 201 Created`() = testApplication {
        val mockHttpClient = createMockHttpClient()

        application {
            install(ServerContentNegotiation) { json() }
            routing { musicRoutes(mockHttpClient) }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val request = CreateMusicRequest(
            title = "Blinding Lights",
            artist = "The Weeknd",
            duration = 200,
            albumImageUrl = "https://example.com/cover.jpg"
        )

        val response = client.post("/api/music") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val created: Music = response.body()
        assertNotNull(created.id)
        assertEquals("Blinding Lights", created.title)
        assertEquals("The Weeknd", created.artist)
        assertEquals(200, created.duration)
    }

    @Test
    fun `GET api music id returns 200 OK when music exists`() = testApplication {
        val mockHttpClient = createMockHttpClient()

        application {
            install(ServerContentNegotiation) { json() }
            routing { musicRoutes(mockHttpClient) }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val repository = MusicRepository()
        val music = repository.createMusic("Levitating", "Dua Lipa", 203, null)

        val response = client.get("/api/music/${music.id}")

        assertEquals(HttpStatusCode.OK, response.status)
        val fetched: Music = response.body()
        assertEquals(music.id, fetched.id)
        assertEquals("Levitating", fetched.title)
    }

    @Test
    fun `GET api music id returns 404 NotFound when music does not exist`() = testApplication {
        val mockHttpClient = createMockHttpClient()

        application {
            install(ServerContentNegotiation) { json() }
            routing { musicRoutes(mockHttpClient) }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/music/non-existent-id")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET api youtube playlist playlistId returns enriched items list`() = testApplication {
        val mockHttpClient = createMockHttpClient()

        application {
            install(ServerContentNegotiation) { json() }
            routing { musicRoutes(mockHttpClient) }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/youtube/playlist/PL12345")

        assertEquals(HttpStatusCode.OK, response.status)
        val enrichedItems: List<YouTubeEnrichedItem> = response.body()
        assertEquals(1, enrichedItems.size)
        assertEquals("vid123", enrichedItems[0].videoId)
        assertEquals("Song Title", enrichedItems[0].title)
        assertEquals("Artist Name", enrichedItems[0].artist)
        assertEquals(225, enrichedItems[0].duration)
    }
}