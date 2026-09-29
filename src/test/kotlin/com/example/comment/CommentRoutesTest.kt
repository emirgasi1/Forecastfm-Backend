package com.example.comment

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.AuthRepository
import com.example.auth.model.RegisterRequest
import com.example.post.PostRepository
import com.example.routes.commentRoutes
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

class CommentRoutesTest {

    private lateinit var userId: String
    private lateinit var postId: String

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        val authRepository = AuthRepository()
        val registerResult = authRepository.register(
            RegisterRequest(
                email = "routecommenter@example.com",
                username = "routecommenter",
                password = "password123",
                bio = null
            )
        )
        userId = registerResult.getOrNull()!!.user.id

        val postRepository = PostRepository()
        val post = postRepository.createPost(userId, "Post for route testing", null)
        postId = post.id
    }

    @Test
    fun `POST api comments creates comment and returns 201 Created`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { commentRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val request = CreateCommentRequest(
            userId = userId,
            postId = postId,
            text = "Great post!"
        )

        val response = client.post("/api/comments") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val createdComment: Comment = response.body()
        assertNotNull(createdComment.id)
        assertEquals("Great post!", createdComment.text)
        assertEquals(userId, createdComment.userId)
        assertEquals(postId, createdComment.postId)
    }

    @Test
    fun `GET api comments by id returns 200 OK when comment exists`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { commentRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val commentRepo = CommentRepository()
        val comment = commentRepo.createComment(userId, postId, "Test comment")

        val response = client.get("/api/comments/${comment.id}")

        assertEquals(HttpStatusCode.OK, response.status)
        val fetchedComment: Comment = response.body()
        assertEquals(comment.id, fetchedComment.id)
        assertEquals("Test comment", fetchedComment.text)
    }

    @Test
    fun `GET api comments by id returns 404 NotFound for non-existent comment`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { commentRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val response = client.get("/api/comments/non-existent-id")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun `POST api comments id like creates a like and returns updated count`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { commentRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val commentRepo = CommentRepository()
        val comment = commentRepo.createComment(userId, postId, "Likeable comment")

        val response = client.post("/api/comments/${comment.id}/like") {
            header("User-Id", userId)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body: Map<String, Int> = response.body()
        assertEquals(1, body["likes"])
    }

    @Test
    fun `POST api comments id like returns 400 BadRequest when User-Id header is missing`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { commentRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val commentRepo = CommentRepository()
        val comment = commentRepo.createComment(userId, postId, "Comment without user header")

        val response = client.post("/api/comments/${comment.id}/like")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `DELETE api comments id like removes like and returns updated count`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { commentRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val commentRepo = CommentRepository()
        val comment = commentRepo.createComment(userId, postId, "Unlikeable comment")
        commentRepo.likeComment(comment.id, userId)

        val response = client.delete("/api/comments/${comment.id}/like") {
            header("User-Id", userId)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val body: Map<String, Int> = response.body()
        assertEquals(0, body["likes"])
    }

    @Test
    fun `DELETE api comments id like returns 400 BadRequest when User-Id header is missing`() = testApplication {
        application {
            install(ServerContentNegotiation) { json() }
            routing { commentRoutes() }
        }
        val client = createClient { install(ClientContentNegotiation) { json() } }

        val commentRepo = CommentRepository()
        val comment = commentRepo.createComment(userId, postId, "Comment for missing delete header")

        val response = client.delete("/api/comments/${comment.id}/like")

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}