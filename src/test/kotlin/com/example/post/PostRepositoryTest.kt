package com.example.post

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.AuthRepository
import com.example.auth.model.RegisterRequest
import com.example.post.PostRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue


class PostRepositoryTest {

    private lateinit var postRepository: PostRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var userId: String

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()
        postRepository = PostRepository()
        authRepository = AuthRepository()

        val registerResult = authRepository.register(
            RegisterRequest(
                email = "poster@example.com",
                username = "poster",
                password = "password123",
                bio = null
            )
        )
        assertTrue(
            registerResult.isSuccess,
            "Setup register failed: ${registerResult.exceptionOrNull()}"
        )
        userId = registerResult.getOrNull()!!.user.id
    }

    @Test
    fun `createPost with valid data returns Post with generated id`() {
        val post = postRepository.createPost(
            userId = userId,
            caption = "First post",
            imageUrl = "https://example.com/image.jpg"
        )

        assertTrue(post.id.isNotEmpty())
        assertEquals(userId, post.userId)
        assertEquals("First post", post.caption)
        assertEquals("https://example.com/image.jpg", post.imageUrl)
        assertTrue(post.createdAt.isNotEmpty())
    }

    @Test
    fun `createPost with null caption stores null caption`() {
        val post = postRepository.createPost(
            userId = userId,
            caption = null,
            imageUrl = "https://example.com/image.jpg"
        )

        assertNull(post.caption)
        assertTrue(post.id.isNotEmpty())
    }

    @Test
    fun `createPost with null imageUrl stores null imageUrl`() {
        val post = postRepository.createPost(
            userId = userId,
            caption = "Text only",
            imageUrl = null
        )

        assertNull(post.imageUrl)
        assertTrue(post.id.isNotEmpty())
    }

    @Test
    fun `createPost with non-existent userId throws`() {
        var threw = false
        try {
            postRepository.createPost(
                userId = "non-existent-user-id",
                caption = "Orphan post",
                imageUrl = null
            )
        } catch (e: Exception) {
            threw = true
        }
        assertTrue(threw, "Expected FK violation but no exception was thrown")
    }

    @Test
    fun `getPosts returns empty list when no posts exist`() {
        val posts = postRepository.getPosts()

        assertTrue(posts.isEmpty())
    }

    @Test
    fun `getPosts returns all posts`() {
        postRepository.createPost(userId, "Post 1", null)
        postRepository.createPost(userId, "Post 2", null)
        postRepository.createPost(userId, "Post 3", null)

        val posts = postRepository.getPosts()

        assertEquals(3, posts.size)
    }

    @Test
    fun `getPostById returns the correct post`() {
        val created = postRepository.createPost(userId, "Find me", null)

        val found = postRepository.getPostById(created.id)

        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals("Find me", found.caption)
    }

    @Test
    fun `getPostById returns null for non-existent id`() {
        val found = postRepository.getPostById("non-existent-id")

        assertNull(found)
    }

    @Test
    fun `getPostsByUserId returns only that user posts`() {
        val otherResult = authRepository.register(
            RegisterRequest(
                email = "other@example.com",
                username = "other",
                password = "password123",
                bio = null
            )
        )
        assertTrue(otherResult.isSuccess)
        val otherUserId = otherResult.getOrNull()!!.user.id

        postRepository.createPost(userId, "Mine 1", null)
        postRepository.createPost(userId, "Mine 2", null)
        postRepository.createPost(otherUserId, "Theirs", null)

        val mine = postRepository.getPostsByUserId(userId)
        val theirs = postRepository.getPostsByUserId(otherUserId)

        assertEquals(2, mine.size)
        assertEquals(1, theirs.size)
        assertTrue(mine.all { it.userId == userId })
        assertTrue(theirs.all { it.userId == otherUserId })
    }

    @Test
    fun `updatePostImage changes the imageUrl`() {
        val created = postRepository.createPost(userId, "Before", "https://old.com/img.jpg")

        val updated = postRepository.updatePostImage(
            postId = created.id,
            imageUrl = "https://new.com/img.jpg"
        )

        assertEquals(created.id, updated.id)
        assertEquals("https://new.com/img.jpg", updated.imageUrl)
        assertEquals("Before", updated.caption)
    }

    @Test
    fun `updatePostImage with non-existent post throws`() {
        var threw = false
        try {
            postRepository.updatePostImage(
                postId = "non-existent-id",
                imageUrl = "https://new.com/img.jpg"
            )
        } catch (e: Exception) {
            threw = true
        }
        assertTrue(threw, "Expected exception for non-existent post")
    }
}