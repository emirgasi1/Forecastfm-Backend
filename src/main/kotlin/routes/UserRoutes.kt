package com.example.routes

import com.example.post.PostRepository
import com.example.post.SavedPostRepository
import com.example.profile.ProfileRepository
import com.example.user.UpdateProfileRequest
import com.example.user.UserRepository
import com.example.user.UserResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import java.io.File
import java.util.UUID

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

    put("/api/users/{id}") {
        val id = call.parameters["id"]
            ?: return@put call.respond(HttpStatusCode.BadRequest, "Missing user ID")

        val request = call.receive<UpdateProfileRequest>()
        val userRepository = UserRepository()

        val updated = userRepository.updateProfile(
            userId = id,
            username = request.username,
            bio = request.bio,
            favoriteLocation = request.favoriteLocation
        )

        if (updated) {
            call.respond(HttpStatusCode.OK, mapOf("message" to "Profile updated"))
        } else {
            call.respond(HttpStatusCode.NotFound, "User not found")
        }
    }

    post("/api/users/{id}/profile-image") {
        val id = call.parameters["id"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing user ID")

        val multipart = call.receiveMultipart()
        var fileName: String? = null

        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                val originalName = part.originalFileName ?: "image.jpg"
                val ext = originalName.substringAfterLast('.', "jpg")
                val generatedName = "${UUID.randomUUID()}.$ext"
                fileName = generatedName

                val uploadDir = File("uploads")
                if (!uploadDir.exists()) uploadDir.mkdirs()

                val targetFile = File(uploadDir, generatedName)
                part.streamProvider().use { input ->
                    targetFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            part.dispose()
        }

        val uploaded = fileName
        if (uploaded == null) {
            return@post call.respond(HttpStatusCode.BadRequest, "No file uploaded")
        }

        val url = "/uploads/$uploaded"
        val userRepository = UserRepository()
        userRepository.updateProfileImage(id, url)

        call.respond(HttpStatusCode.OK, mapOf("url" to url))
    }
}