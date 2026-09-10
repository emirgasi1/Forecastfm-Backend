package com.example.routes

import com.example.music.MusicHistoryRepository
import com.example.music.musichistory.AddMusicHistoryRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlin.uuid.Uuid

fun Route.musicHistoryRoutes() {
    val musicHistoryRepository = MusicHistoryRepository()

    post("/api/music-history") {
        val request = call.receive<AddMusicHistoryRequest>()

        musicHistoryRepository.addHistory(
            userId = request.userId,
            playlistId = request.playlistId
        )

        call.respond(HttpStatusCode.Created)
    }

    get("/api/music-history/{userId}") {
        val userId = call.parameters["userId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user ID")

        val history = musicHistoryRepository.getHistory(userId)
        call.respond(history)
    }
}