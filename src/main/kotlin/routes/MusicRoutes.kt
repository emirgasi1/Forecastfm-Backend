package com.example.routes

import com.example.music.CreateMusicRequest
import com.example.music.MusicRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlin.uuid.Uuid

fun Route.musicRoutes(){

    val musicRepository= MusicRepository()

    post("/api/music") {

        val request = call.receive<CreateMusicRequest>()

        val music = musicRepository.createMusic(
            title = request.title,
            artist = request.artist,
            duration = request.duration,
            albumImageUrl = request.albumImageUrl
        )

        call.respond(
            HttpStatusCode.Created,
            music
        )
    }

    get("/api/music/{id}") {

        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        val musicId = try {
            Uuid.parse(id)
        } catch (e: IllegalArgumentException) {
            return@get call.respond(HttpStatusCode.BadRequest)
        }

        val music = musicRepository.getMusicById(musicId)

        if (music == null) {
            call.respond(HttpStatusCode.NotFound)
        } else {
            call.respond(music)
        }
    }
}