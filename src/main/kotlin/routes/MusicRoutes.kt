package com.example.routes

import com.example.music.CreateMusicRequest
import com.example.music.MusicRepository
import com.example.youtube.YouTubeApi
import com.example.youtube.YouTubeEnrichedItem
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.musicRoutes(httpClient: HttpClient) {
    val musicRepository = MusicRepository()
    val youTubeApi = YouTubeApi(httpClient)

    post("/api/music") {
        val request = call.receive<CreateMusicRequest>()

        val music = musicRepository.createMusic(
            title = request.title,
            artist = request.artist,
            duration = request.duration,
            albumImageUrl = request.albumImageUrl
        )

        call.respond(HttpStatusCode.Created, music)
    }

    get("/api/music/{id}") {
        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing music ID")

        val music = musicRepository.getMusicById(id)

        if (music == null) {
            call.respond(HttpStatusCode.NotFound, "Music not found")
        } else {
            call.respond(music)
        }
    }

    get("/api/youtube/playlist/{playlistId}") {
        val playlistId = call.parameters["playlistId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing playlist ID")

        try {
            val items = youTubeApi.getPlaylistItems(playlistId)

            val videoIds = items.mapNotNull { it.snippet?.resourceId?.videoId }
            val durations = youTubeApi.getVideoDurations(videoIds)

            val enriched = items.mapNotNull { item ->
                val snippet = item.snippet ?: return@mapNotNull null
                val videoId = snippet.resourceId?.videoId ?: return@mapNotNull null

                YouTubeEnrichedItem(
                    videoId = videoId,
                    title = snippet.title,
                    artist = snippet.videoOwnerChannelTitle ?: "Unknown",
                    duration = durations[videoId] ?: 0,
                    thumbnailUrl = snippet.thumbnails?.medium?.url
                        ?: snippet.thumbnails?.high?.url
                        ?: ""
                )
            }

            call.respond(enriched)
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "Failed to fetch YouTube playlist")
        }
    }
}