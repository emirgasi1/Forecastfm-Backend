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

        val userId = try {
            Uuid.parse(request.userId)
        } catch (e: IllegalArgumentException) {
            return@post call.respond(HttpStatusCode.BadRequest)
        }

        val playlistId = try {
            Uuid.parse(request.playlistId)
        } catch (e: IllegalArgumentException) {
            return@post call.respond(HttpStatusCode.BadRequest)
        }

        musicHistoryRepository.addHistory(
            userId = userId,
            playlistId = playlistId
        )

        call.respond(HttpStatusCode.Created)
    }

    get("/api/music-history/{userId}") {

        val userId = call.parameters["userId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        val parsedUserId = try {
            Uuid.parse(userId)
        } catch (e: IllegalArgumentException) {
            return@get call.respond(HttpStatusCode.BadRequest)
        }

        val history =
            musicHistoryRepository.getHistory(parsedUserId)

        call.respond(history)
    }
}