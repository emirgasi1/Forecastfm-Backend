package com.example.routes

import com.example.comment.CommentRepository
import com.example.like.CreateLikeRequest
import com.example.like.LikeRepository
import com.example.post.CreatePostRequest
import com.example.post.PostRepository
import com.example.post.SavePostRequest
import com.example.post.SavedPostRepository
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.coroutines.runBlocking
import storage.B2Storage
import java.util.UUID

fun Route.postRoutes() {
    val postRepository = PostRepository()
    val commentRepository = CommentRepository()
    val likeRepository = LikeRepository()
    val savedPostRepository = SavedPostRepository()

    post("/api/posts") {
        val request = call.receive<CreatePostRequest>()
        val post = postRepository.createPost(
            userId = request.userId,
            caption = request.caption,
            imageUrl = request.imageUrl,
            outfitId = request.outfitId
        )
        call.respond(HttpStatusCode.Created, post)
    }

    get("/api/posts") {
        val posts = postRepository.getPosts()
        val enriched = posts.map { post ->
            val likes = likeRepository.getPostLikeCount(post.id)
            val commentCount = commentRepository.getCommentsByPostId(post.id).size
            val presignedImageUrl = post.imageUrl?.let { key ->
                runCatching { B2Storage.presign(key) }.getOrNull()
            }
            post.copy(
                imageUrl = presignedImageUrl,
                likes = likes,
                commentCount = commentCount
            )
        }
        call.respond(enriched)
    }

    get("/api/posts/{id}") {
        val id = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing post ID")

        val post = postRepository.getPostById(id)
        if (post == null) {
            call.respond(HttpStatusCode.NotFound, "Post not found")
        } else {
            val likes = likeRepository.getPostLikeCount(post.id)
            val commentCount = commentRepository.getCommentsByPostId(post.id).size
            val presignedImageUrl = post.imageUrl?.let { key ->
                runCatching { B2Storage.presign(key) }.getOrNull()
            }
            call.respond(
                post.copy(
                    imageUrl = presignedImageUrl,
                    likes = likes,
                    commentCount = commentCount
                )
            )
        }
    }

    get("/api/posts/{postId}/comments") {
        val postId = call.parameters["postId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing post ID")

        val comments = commentRepository.getCommentsByPostId(postId)
        call.respond(comments)
    }

    post("/api/posts/{postId}/like") {
        val postId = call.parameters["postId"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing post ID")

        val request = call.receive<CreateLikeRequest>()
        likeRepository.likePost(userId = request.userId, postId = postId)
        call.respond(HttpStatusCode.Created)
    }

    delete("/api/posts/{postId}/like") {
        val postId = call.parameters["postId"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing post ID")

        val userId = call.request.queryParameters["userId"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing user ID")

        likeRepository.unlikePost(userId = userId, postId = postId)
        call.respond(HttpStatusCode.OK)
    }

    get("/api/posts/{postId}/like") {
        val postId = call.parameters["postId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing post ID")

        val userId = call.request.queryParameters["userId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user ID")

        val liked = likeRepository.isPostLiked(userId = userId, postId = postId)
        call.respond(mapOf("liked" to liked))
    }

    get("/api/posts/{postId}/likes") {
        val postId = call.parameters["postId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing post ID")

        val count = likeRepository.getPostLikeCount(postId)
        call.respond(mapOf("likes" to count))
    }

    post("/api/posts/{postId}/save") {
        val postId = call.parameters["postId"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing post ID")

        val request = call.receive<SavePostRequest>()
        savedPostRepository.savePost(userId = request.userId, postId = postId)
        call.respond(HttpStatusCode.Created)
    }

    delete("/api/posts/{postId}/save") {
        val postId = call.parameters["postId"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing post ID")

        val userId = call.request.queryParameters["userId"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing user ID")

        savedPostRepository.unsavePost(userId = userId, postId = postId)
        call.respond(HttpStatusCode.OK)
    }

    get("/api/posts/{postId}/save") {
        val postId = call.parameters["postId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing post ID")

        val userId = call.request.queryParameters["userId"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user ID")

        val saved = savedPostRepository.isPostSaved(userId = userId, postId = postId)
        call.respond(mapOf("saved" to saved))
    }

    post("/api/posts/{postId}/image") {
        val postId = call.parameters["postId"]
            ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing post ID")

        try {
            val multipart = call.receiveMultipart()
            var uploadedKey: String? = null

            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val fileName = part.originalFileName ?: "image.jpg"
                    val extension = fileName.substringAfterLast(".", "jpg")
                    val key = "posts/$postId-${UUID.randomUUID()}.$extension"

                    val bytes = part.streamProvider().use { it.readBytes() }

                    runBlocking {
                        B2Storage.upload(
                            key = key,
                            bytes = bytes,
                            contentType = "image/$extension"
                        )
                    }

                    uploadedKey = key
                }
                part.dispose()
            }

            if (uploadedKey != null) {
                postRepository.updatePostImage(postId, uploadedKey)
                call.respond(HttpStatusCode.OK, mapOf("imageUrl" to uploadedKey))
            } else {
                call.respond(HttpStatusCode.BadRequest, "No image file provided")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            call.respond(HttpStatusCode.InternalServerError, "Upload failed: ${e.message}")
        }
    }

    get("/api/posts/saved") {
        val userId = call.request.headers["User-Id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing User-Id")

        val savedPosts = savedPostRepository.getSavedPosts(userId)
        val enriched = savedPosts.map { post ->
            val presignedImageUrl = post.imageUrl?.let { key ->
                runCatching { B2Storage.presign(key) }.getOrNull()
            }
            post.copy(imageUrl = presignedImageUrl)
        }
        call.respond(enriched)
    }
}