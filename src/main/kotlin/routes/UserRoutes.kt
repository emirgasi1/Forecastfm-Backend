package com.example.routes

import com.example.post.PostRepository
import com.example.post.SavedPostRepository
import com.example.profile.ProfileRepository
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

fun Route.userRoutes() {
    val postRepository = PostRepository()
    val savedPostRepository = SavedPostRepository()
    val profileRepository = ProfileRepository()

    get("/api/users/{id}") {
        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "User ID is required")

        val userRepository = UserRepository()
        val user = userRepository.getUserById(id)

        if (user == null) {
            call.respond(HttpStatusCode.NotFound, "User not found")
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


    get("/api/users/{userId}/saved-posts") {
        val userId = call.parameters["userId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user ID")

        val posts = savedPostRepository.getSavedPosts(userId)
        call.respond(posts)
    }

    get("/api/users/{userId}/posts") {
        val userId = call.parameters["userId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user ID")

        val posts = postRepository.getPostsByUserId(userId)
        call.respond(posts)
    }

    get("/api/users/{userId}/profile") {
        val userId = call.parameters["userId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user ID")


        try {
            val profile = profileRepository.getProfile(userId)
            call.respond(profile)
        } catch (e: IllegalArgumentException) {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }
}