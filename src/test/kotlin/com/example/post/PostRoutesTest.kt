package com.example.post

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.AuthRepository
import com.example.auth.model.RegisterRequest
import com.example.comment.CommentRepository
import com.example.like.CreateLikeRequest
import com.example.routes.postRoutes
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation as ServerContentNegotiation
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PostRoutesTest {

    private lateinit var userId: String

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        val authRepository = AuthRepository()
        val registerResult = authRepository.register(
            RegisterRequest(
                email = "postuser@example.com",
                username = "postuser",
                password = "password123",
                bio = null
            )
        )
        userId = registerResult.getOrNull()!!.user.id
    }

    @Test
    fun `POST api posts creates post and returns 201 Created`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { postRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val request = CreatePostRequest(
            userId = userId,
            caption = "My first post",
            imageUrl = "http://example.com/image.jpg"
        )

        val response = client.post("/api/posts") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val createdPost: Post = response.body()
        assertNotNull(createdPost.id)
        assertEquals("My first post", createdPost.caption)
        assertEquals(userId, createdPost.userId)
    }

    @Test
    fun `GET api posts returns list of posts`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { postRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val postRepo = PostRepository()
        postRepo.createPost(userId, "Post 1", null)
        postRepo.createPost(userId, "Post 2", null)

        val response = client.get("/api/posts")

        assertEquals(HttpStatusCode.OK, response.status)
        val posts: List<Post> = response.body()
        assertEquals(2, posts.size)
    }

    @Test
    fun `GET api posts by id returns 200 OK when post exists`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { postRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val postRepo = PostRepository()
        val post = postRepo.createPost(userId, "Target post", null)

        val response = client.get("/api/posts/${post.id}")

        assertEquals(HttpStatusCode.OK, response.status)
        val fetchedPost: Post = response.body()
        assertEquals(post.id, fetchedPost.id)
        assertEquals("Target post", fetchedPost.caption)
    }

    @Test
    fun `GET api posts by id returns 404 NotFound for non-existent post`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { postRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/posts/non-existent-id")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `GET api posts postId comments returns comments for post`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { postRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val postRepo = PostRepository()
        val commentRepo = CommentRepository()

        val post = postRepo.createPost(userId, "Post with comments", null)
        commentRepo.createComment(userId, post.id, "Comment 1")
        commentRepo.createComment(userId, post.id, "Comment 2")

        val response = client.get("/api/posts/${post.id}/comments")

        assertEquals(HttpStatusCode.OK, response.status)
        val comments: List<com.example.comment.Comment> = response.body()
        assertEquals(2, comments.size)
    }

    @Test
    fun `POST, GET, and DELETE post like endpoints work as expected`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { postRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val postRepo = PostRepository()
        val post = postRepo.createPost(userId, "Likable post", null)

        // 1. Like post
        val likeResponse = client.post("/api/posts/${post.id}/like") {
            contentType(ContentType.Application.Json)
            setBody(CreateLikeRequest(userId = userId))
        }
        assertEquals(HttpStatusCode.Created, likeResponse.status)

        // 2. Check if liked
        val isLikedResponse = client.get("/api/posts/${post.id}/like?userId=$userId")
        assertEquals(HttpStatusCode.OK, isLikedResponse.status)
        val isLikedBody: Map<String, Boolean> = isLikedResponse.body()
        assertEquals(true, isLikedBody["liked"])

        // 3. Get total likes count
        val countResponse = client.get("/api/posts/${post.id}/likes")
        assertEquals(HttpStatusCode.OK, countResponse.status)
        val countBody: Map<String, Int> = countResponse.body()
        assertEquals(1, countBody["likes"])

        // 4. Unlike post
        val unlikeResponse = client.delete("/api/posts/${post.id}/like?userId=$userId")
        assertEquals(HttpStatusCode.OK, unlikeResponse.status)

        // 5. Verify unliked
        val isLikedAfterResponse = client.get("/api/posts/${post.id}/like?userId=$userId")
        val isLikedAfterBody: Map<String, Boolean> = isLikedAfterResponse.body()
        assertEquals(false, isLikedAfterBody["liked"])
    }

    @Test
    fun `POST, GET, and DELETE post save endpoints work as expected`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { postRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val postRepo = PostRepository()
        val post = postRepo.createPost(userId, "Saveable post", null)

        // 1. Save post
        val saveResponse = client.post("/api/posts/${post.id}/save") {
            contentType(ContentType.Application.Json)
            setBody(SavePostRequest(userId = userId))
        }
        assertEquals(HttpStatusCode.Created, saveResponse.status)

        // 2. Check if saved
        val isSavedResponse = client.get("/api/posts/${post.id}/save?userId=$userId")
        assertEquals(HttpStatusCode.OK, isSavedResponse.status)
        val isSavedBody: Map<String, Boolean> = isSavedResponse.body()
        assertEquals(true, isSavedBody["saved"])

        // 3. Get user's saved posts list
        val getSavedResponse = client.get("/api/posts/saved") {
            header("User-Id", userId)
        }
        assertEquals(HttpStatusCode.OK, getSavedResponse.status)
        val savedPosts: List<Post> = getSavedResponse.body()
        assertEquals(1, savedPosts.size)
        assertEquals(post.id, savedPosts[0].id)

        // 4. Unsave post
        val unsaveResponse = client.delete("/api/posts/${post.id}/save?userId=$userId")
        assertEquals(HttpStatusCode.OK, unsaveResponse.status)

        // 5. Verify unsaved
        val isSavedAfterResponse = client.get("/api/posts/${post.id}/save?userId=$userId")
        val isSavedAfterBody: Map<String, Boolean> = isSavedAfterResponse.body()
        assertEquals(false, isSavedAfterBody["saved"])
    }

    @Test
    fun `GET api posts saved returns 400 BadRequest when User-Id header is missing`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { postRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/posts/saved")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}