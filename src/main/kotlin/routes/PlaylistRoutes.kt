package com.example.routes

import com.example.playlist.AddSongToPlaylistRequest
import com.example.playlist.CreatePlaylistRequest
import com.example.playlist.PlaylistRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import playlist.UpdatePlaylistImageRequest
import kotlin.uuid.Uuid

fun Route.playlistRoutes() {
    val playlistRepository = PlaylistRepository()

    post("/api/playlists") {
        val request = call.receive<CreatePlaylistRequest>()
        val playlist = playlistRepository.createPlaylist(
            title = request.title,
            genre = request.genre,
            mood = request.mood,
            albumImageUrl = request.albumImageUrl,
            weather = request.weather,
            temperature = request.temperature,
            location = request.location,
            spotifyUrl = request.spotifyUrl,
            youtubeUrl = request.youtubeUrl
        )
        call.respond(HttpStatusCode.Created, playlist)
    }

    get("/api/playlists") {
        val playlists = playlistRepository.getPlaylists()
        call.respond(playlists)
    }

    get("/api/playlists/{id}") {
        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing playlist ID")

        val playlist = playlistRepository.getPlaylistById(id)
        if (playlist == null) {
            call.respond(HttpStatusCode.NotFound, "Playlist not found")
        } else {
            call.respond(playlist)
        }
    }

    post("/api/playlists/{playlistId}/songs") {
        val playlistId = call.parameters["playlistId"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing playlist ID")

        val request = call.receive<AddSongToPlaylistRequest>()

        playlistRepository.addSongToPlaylist(
            playlistId = playlistId,
            musicId = request.musicId
        )
        call.respond(HttpStatusCode.Created)
    }

    post("/api/playlists/{playlistId}/favorite") {
        val playlistId = call.parameters["playlistId"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid playlist ID")

        val userId = call.request.headers["User-Id"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        playlistRepository.favoritePlaylist(
            userId = userId,
            playlistId = playlistId
        )
        call.respond(HttpStatusCode.OK)
    }

    delete("/api/playlists/{playlistId}/favorite") {
        val playlistId = call.parameters["playlistId"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid playlist ID")

        val userId = call.request.headers["User-Id"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        playlistRepository.unfavoritePlaylist(
            userId = userId,
            playlistId = playlistId
        )
        call.respond(HttpStatusCode.OK)
    }

    get("/api/playlists/favorites") {
        val userId = call.request.headers["User-Id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        val favoriteIds = playlistRepository.getFavoritePlaylistIds(userId)
        call.respond(favoriteIds)
    }

    get("/api/playlists/{playlistId}/songs") {
        val playlistId = call.parameters["playlistId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing playlist ID")

        val songs = playlistRepository.getSongsForPlaylist(playlistId)
        call.respond(songs)
    }
    get("/api/playlists/saved") {
        val userId = call.request.headers["User-Id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        val savedPlaylists = playlistRepository.getFavoritePlaylists(userId)
        call.respond(savedPlaylists)
    }
    put("/api/playlists/{playlistId}/image") {
        val playlistId = call.parameters["playlistId"]
            ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing playlist ID")

        val request = call.receive<UpdatePlaylistImageRequest>()

        playlistRepository.updatePlaylistImage(
            playlistId = playlistId,
            imageUrl = request.imageUrl
        )
        call.respond(HttpStatusCode.OK)
    }
}