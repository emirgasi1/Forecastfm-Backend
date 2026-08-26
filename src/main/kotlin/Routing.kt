package com.example

import com.example.comment.CommentRepository
import com.example.comment.CreateCommentRequest
import com.example.like.CreateLikeRequest
import com.example.like.LikeRepository
import com.example.post.CreatePostRequest
import com.example.post.PostRepository
import com.example.model.HealthResponse
import com.example.music.CreateMusicRequest
import com.example.music.MusicRepository
import com.example.playlist.AddSongToPlaylistRequest
import com.example.playlist.CreatePlaylistRequest
import com.example.playlist.PlaylistRepository
import com.example.post.SavePostRequest
import com.example.post.SavedPostRepository
import com.example.profile.ProfileRepository
import com.example.routes.commentRoutes
import com.example.routes.musicRoutes
import com.example.routes.playlistRoutes
import com.example.routes.postRoutes
import com.example.routes.userRoutes
import com.example.user.CreateUserRequest
import com.example.user.UserRepository
import com.example.user.UserResponse
import io.ktor.client.HttpClient
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.receive
import kotlin.collections.mapOf
import kotlin.uuid.Uuid

fun Application.configureRouting(
    httpClient: HttpClient
) {

    routing {
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







    }
}