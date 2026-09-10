package com.example


import com.example.routes.authRoutes
import com.example.routes.commentRoutes
import com.example.routes.locationRoutes
import com.example.routes.musicHistoryRoutes
import com.example.routes.musicRoutes
import com.example.routes.outfitRoutes
import com.example.routes.placeRecommendationRoutes
import com.example.routes.placesRoutes
import com.example.routes.playlistRoutes
import com.example.routes.postRoutes
import com.example.routes.userRoutes
import com.example.user.CreateUserRequest
import com.example.user.UserRepository
import com.example.user.UserResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.http.content.static
import io.ktor.server.http.content.staticFiles
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.receive
import java.io.File
import kotlin.collections.mapOf
import kotlin.uuid.Uuid

fun Application.configureRouting(
    httpClient: HttpClient
) {

    routing {
        static("/uploads") {
            staticFiles("/", File("uploads"))
        }
        get("/api/health") {
            call.respond(
                mapOf("status" to "ok")
            )
        }
        userRoutes()
        postRoutes()
        commentRoutes()
        playlistRoutes()
        musicRoutes()
        musicHistoryRoutes()
        locationRoutes()
        authRoutes()
        outfitRoutes()
        placeRecommendationRoutes()
        placesRoutes()







    }
}