package com.example.routes

import com.example.user.CreateUserRequest
import com.example.user.UserRepository
import com.example.user.UserResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlin.uuid.Uuid

fun Route.userRoutes(){
    get("/api/users/{id}") {

        val idString = call.parameters["id"]

        if (idString == null) {
            call.respond(
                HttpStatusCode.BadRequest,
                "User ID is required"
            )
            return@get
        }

        val id = try {
            Uuid.parse(idString)
        } catch (e: IllegalArgumentException) {
            call.respond(
                HttpStatusCode.BadRequest,
                "Invalid user ID"
            )
            return@get
        }

        val userRepository = UserRepository()

        val user = userRepository.getUserById(id)

        if (user == null) {
            call.respond(
                HttpStatusCode.NotFound,
                "User not found"
            )
            return@get
        }

        call.respond(
            UserResponse(
                id = user.id,
                username = user.username,
                bio = user.bio,
                profileImageUrl = user.profileImageUrl,
                favoriteLocation = user.favoriteLocation
            )
        )
    }
    post("/api/users") {
        val request = call.receive<CreateUserRequest>()

        val userRepository = UserRepository()

        val userId = userRepository.createUser(
            username = request.username,
            bio = request.bio,
            profileImageUrl = request.profileImageUrl,
            favoriteLocation = request.favoriteLocation
        )

        call.respond(
            HttpStatusCode.Created,
            mapOf("id" to userId)
        )
    }

}