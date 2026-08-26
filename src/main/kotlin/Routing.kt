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
import com.example.user.CreateUserRequest
import com.example.user.UserRepository
import com.example.user.UserResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.receive
import kotlin.collections.mapOf
import kotlin.uuid.Uuid

fun Application.configureRouting() {
    val postRepository = PostRepository()
    val commentRepository = CommentRepository()
    val musicRepository = MusicRepository()
    val playlistRepository = PlaylistRepository()
    val likeRepository = LikeRepository()
    val savedPostRepository = SavedPostRepository()
    val profileRepository = ProfileRepository()
    routing {
        get("/api/health"){
            call.respond(HealthResponse("Ok"))

        }



        post("/api/posts") {
            val request = call.receive<CreatePostRequest>()

            val post = postRepository.createPost(
                userId = Uuid.parse(request.userId),
                caption = request.caption,
                imageUrl = request.imageUrl
            )

            call.respond(HttpStatusCode.Created, post)
        }

        get("/api/posts") {

            val posts = postRepository.getPosts()

            call.respond(posts)
        }


        get("/api/posts/{id}") {
            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val postId = try {
                Uuid.parse(id)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val post = postRepository.getPostById(postId)

            if (post == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(post)
            }
        }

        post("/api/comments") {

            val request = call.receive<CreateCommentRequest>()

            val comment = commentRepository.createComment(
                userId = Uuid.parse(request.userId),
                postId = Uuid.parse(request.postId),
                text = request.text
            )

            call.respond(
                HttpStatusCode.Created,
                comment
            )
        }

        get("/api/comments/{id}") {

            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val commentId = try {
                Uuid.parse(id)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val comment = commentRepository.getCommentById(commentId)

            if (comment == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(comment)
            }
        }

        get("/api/posts/{postId}/comments") {

            val postId = call.parameters["postId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val parsedPostId = try {
                Uuid.parse(postId)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val comments = commentRepository.getCommentsByPostId(
                parsedPostId
            )

            call.respond(comments)
        }


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

        post("/api/playlists") {

            val request = call.receive<CreatePlaylistRequest>()

            val playlist = playlistRepository.createPlaylist(
                title = request.title,
                genre = request.genre,
                mood = request.mood,
                albumImageUrl = request.albumImageUrl,
                weather = request.weather,
                temperature = request.temperature,
                location = request.location
            )

            call.respond(
                HttpStatusCode.Created,
                playlist
            )
        }
        get("/api/playlists"){
            val playlists=
                playlistRepository.getPlaylists()

            call.respond(playlists)
        }
        get("/api/playlists/{id}") {

            val id = call.parameters["id"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val playlistId = try {
                Uuid.parse(id)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val playlist = playlistRepository.getPlaylistById(playlistId)

            if (playlist == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(playlist)
            }
        }
        post("/api/playlists/{playlistId}/songs") {

            val playlistId = call.parameters["playlistId"]
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            val request = call.receive<AddSongToPlaylistRequest>()

            val parsedPlaylistId = try {
                Uuid.parse(playlistId)
            } catch (e: IllegalArgumentException) {
                return@post call.respond(HttpStatusCode.BadRequest)
            }

            val musicId = try {
                Uuid.parse(request.musicId)
            } catch (e: IllegalArgumentException) {
                return@post call.respond(HttpStatusCode.BadRequest)
            }

            playlistRepository.addSongToPlaylist(
                playlistId = parsedPlaylistId,
                musicId = musicId
            )

            call.respond(HttpStatusCode.Created)
        }

        post("/api/playlists/{playlistId}/favorite") {

            val playlistId =
                call.parameters["playlistId"]
                    ?.let { Uuid.parse(it) }
                    ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        "Invalid playlist ID"
                    )

            val userId =
                call.request.headers["User-Id"]
                    ?.let { Uuid.parse(it) }
                    ?: return@post call.respond(
                        HttpStatusCode.BadRequest,
                        "Missing User-Id"
                    )

            playlistRepository.favoritePlaylist(
                userId = userId,
                playlistId = playlistId
            )

            call.respond(HttpStatusCode.OK)
        }
        delete("/api/playlists/{playlistId}/favorite") {

            val playlistId =
                call.parameters["playlistId"]
                    ?.let { Uuid.parse(it) }
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        "Invalid playlist ID"
                    )

            val userId =
                call.request.headers["User-Id"]
                    ?.let { Uuid.parse(it) }
                    ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        "Missing User-Id"
                    )

            playlistRepository.unfavoritePlaylist(
                userId = userId,
                playlistId = playlistId
            )

            call.respond(HttpStatusCode.OK)
        }

        get("/api/playlists/favorites"){
            val userId=
                call.request.headers["User-Id"]
                    ?.let{Uuid.parse(it)}
                    ?:return@get call.respond(
                        HttpStatusCode.BadRequest,
                        "Missing User-Id"
                    )
            val favoriteIds=
                playlistRepository.getFavoritePlaylistIds(userId)

            call.respond(favoriteIds)
        }

        get("/api/playlists/{playlistId}/songs") {

            val playlistId = call.parameters["playlistId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val parsedPlaylistId = try {
                Uuid.parse(playlistId)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val songs = playlistRepository.getSongsForPlaylist(parsedPlaylistId)

            call.respond(songs)
        }

        post("/api/posts/{postId}/like") {

            val postId = call.parameters["postId"]
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            val request = call.receive<CreateLikeRequest>()

            val parsedPostId = try {
                Uuid.parse(postId)
            } catch (e: IllegalArgumentException) {
                return@post call.respond(HttpStatusCode.BadRequest)
            }

            val userId = try {
                Uuid.parse(request.userId)
            } catch (e: IllegalArgumentException) {
                return@post call.respond(HttpStatusCode.BadRequest)
            }

            likeRepository.likePost(
                userId = userId,
                postId = parsedPostId
            )

            call.respond(HttpStatusCode.Created)
        }

        delete("/api/posts/{postId}/like") {

            val postId = call.parameters["postId"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val userId = call.request.queryParameters["userId"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val parsedPostId = try {
                Uuid.parse(postId)
            } catch (e: IllegalArgumentException) {
                return@delete call.respond(HttpStatusCode.BadRequest)
            }

            val parsedUserId = try {
                Uuid.parse(userId)
            } catch (e: IllegalArgumentException) {
                return@delete call.respond(HttpStatusCode.BadRequest)
            }

            likeRepository.unlikePost(
                userId = parsedUserId,
                postId = parsedPostId
            )

            call.respond(HttpStatusCode.OK)
        }

        get("/api/posts/{postId}/like") {

            val postId = call.parameters["postId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val userId = call.request.queryParameters["userId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val parsedPostId = try {
                Uuid.parse(postId)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val parsedUserId = try {
                Uuid.parse(userId)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val liked = likeRepository.isPostLiked(
                userId = parsedUserId,
                postId = parsedPostId
            )

            call.respond(
                mapOf("liked" to liked)
            )
        }

        get("/api/posts/{postId}/likes") {

            val postId = call.parameters["postId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val parsedPostId = try {
                Uuid.parse(postId)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val count = likeRepository.getPostLikeCount(parsedPostId)

            call.respond(
                mapOf("likes" to count)
            )
        }

        post("/api/posts/{postId}/save") {

            val postId = call.parameters["postId"]
                ?: return@post call.respond(HttpStatusCode.BadRequest)

            val request = call.receive<SavePostRequest>()

            val parsedPostId = try {
                Uuid.parse(postId)
            } catch (e: IllegalArgumentException) {
                return@post call.respond(HttpStatusCode.BadRequest)
            }

            val userId = try {
                Uuid.parse(request.userId)
            } catch (e: IllegalArgumentException) {
                return@post call.respond(HttpStatusCode.BadRequest)
            }

            savedPostRepository.savePost(
                userId = userId,
                postId = parsedPostId
            )

            call.respond(HttpStatusCode.Created)
        }

        delete("/api/posts/{postId}/save") {

            val postId = call.parameters["postId"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val userId = call.request.queryParameters["userId"]
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val parsedPostId = try {
                Uuid.parse(postId)
            } catch (e: IllegalArgumentException) {
                return@delete call.respond(HttpStatusCode.BadRequest)
            }

            val parsedUserId = try {
                Uuid.parse(userId)
            } catch (e: IllegalArgumentException) {
                return@delete call.respond(HttpStatusCode.BadRequest)
            }

            savedPostRepository.unsavePost(
                userId = parsedUserId,
                postId = parsedPostId
            )

            call.respond(HttpStatusCode.OK)
        }

        get("/api/posts/{postId}/save") {

            val postId = call.parameters["postId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val userId = call.request.queryParameters["userId"]
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val parsedPostId = try {
                Uuid.parse(postId)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val parsedUserId = try {
                Uuid.parse(userId)
            } catch (e: IllegalArgumentException) {
                return@get call.respond(HttpStatusCode.BadRequest)
            }

            val saved = savedPostRepository.isPostSaved(
                userId = parsedUserId,
                postId = parsedPostId
            )

            call.respond(
                mapOf("saved" to saved)
            )
        }




    }
}