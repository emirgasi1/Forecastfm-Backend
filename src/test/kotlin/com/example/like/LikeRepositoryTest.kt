package com.example.like

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.AuthRepository
import com.example.auth.model.RegisterRequest
import com.example.post.PostRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class LikeRepositoryTest {

    private lateinit var likeRepository: LikeRepository
    private lateinit var postRepository: PostRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var userId: String
    private lateinit var postId: String

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        likeRepository = LikeRepository()
        postRepository = PostRepository()
        authRepository = AuthRepository()

        val registerResult = authRepository.register(
            RegisterRequest(
                email = "liker@example.com",
                username = "liker",
                password = "password123",
                bio = null
            )
        )
        assertTrue(registerResult.isSuccess)
        userId = registerResult.getOrNull()!!.user.id

        val post = postRepository.createPost(userId, "Post for likes", null)
        postId = post.id
    }

    @Test
    fun `likePost creates a like`() {
        likeRepository.likePost(userId, postId)

        assertTrue(likeRepository.isPostLiked(userId, postId))
    }

    @Test
    fun `unlikePost removes a like`() {
        likeRepository.likePost(userId, postId)
        likeRepository.unlikePost(userId, postId)

        assertFalse(likeRepository.isPostLiked(userId, postId))
    }

    @Test
    fun `isPostLiked returns false when no like exists`() {
        val isLiked = likeRepository.isPostLiked(userId, postId)

        assertFalse(isLiked)
    }

    @Test
    fun `getPostLikeCount returns zero when no likes exist`() {
        val count = likeRepository.getPostLikeCount(postId)

        assertEquals(0, count)
    }

    @Test
    fun `getPostLikeCount returns correct count after likes`() {
        val otherUserResult = authRepository.register(
            RegisterRequest(
                email = "other@example.com",
                username = "otherliker",
                password = "password123",
                bio = null
            )
        )
        val otherUserId = otherUserResult.getOrNull()!!.user.id

        likeRepository.likePost(userId, postId)
        likeRepository.likePost(otherUserId, postId)

        assertEquals(2, likeRepository.getPostLikeCount(postId))
    }

    @Test
    fun `getPostLikeCount is independent per post`() {
        val postA = postRepository.createPost(userId, "Post A", null)
        val postB = postRepository.createPost(userId, "Post B", null)

        likeRepository.likePost(userId, postA.id)
        likeRepository.likePost(userId, postB.id)

        assertEquals(1, likeRepository.getPostLikeCount(postA.id))
        assertEquals(1, likeRepository.getPostLikeCount(postB.id))
    }

    @Test
    fun `getUserReceivedLikeCount returns total likes across user posts`() {
        val otherUserResult = authRepository.register(
            RegisterRequest(
                email = "other@example.com",
                username = "otherliker",
                password = "password123",
                bio = null
            )
        )
        val otherUserId = otherUserResult.getOrNull()!!.user.id

        val postA = postRepository.createPost(userId, "Post A", null)
        val postB = postRepository.createPost(userId, "Post B", null)

        likeRepository.likePost(otherUserId, postA.id)
        likeRepository.likePost(otherUserId, postB.id)

        val received = likeRepository.getUserReceivedLikeCount(userId)

        assertEquals(2, received)
    }

    @Test
    fun `like non-existent post throws`() {
        var threw = false
        try {
            likeRepository.likePost(userId, "non-existent-post")
        } catch (e: Exception) {
            threw = true
        }
        assertTrue(threw, "Expected FK violation but no exception was thrown")
    }

    @Test
    fun `like with non-existent user throws`() {
        var threw = false
        try {
            likeRepository.likePost("non-existent-user", postId)
        } catch (e: Exception) {
            threw = true
        }
        assertTrue(threw, "Expected FK violation but no exception was thrown")
    }
}