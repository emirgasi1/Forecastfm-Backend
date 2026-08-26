package com.example.routes

import com.example.comment.CommentRepository
import com.example.like.CreateLikeRequest
import com.example.like.LikeRepository
import com.example.post.CreatePostRequest
import com.example.post.PostRepository
import com.example.post.SavePostRequest
import com.example.post.SavedPostRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlin.uuid.Uuid

fun Route.postRoutes(){

    val postRepository= PostRepository()
    val commentRepository= CommentRepository()
    val likeRepository= LikeRepository()
    val savedPostRepository= SavedPostRepository()

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