package com.example.comment

import com.example.TestDataCleaner
import com.example.TestDatabase
import com.example.auth.AuthRepository
import com.example.auth.model.RegisterRequest
import com.example.post.PostRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue


class CommentRepositoryTest {

    private lateinit var commentRepository: CommentRepository
    private lateinit var postRepository: PostRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var userId: String
    private lateinit var postId: String

    @BeforeTest
    fun setup() {
        TestDatabase.initOnce()
        TestDataCleaner.clearAll()

        commentRepository = CommentRepository()
        postRepository = PostRepository()
        authRepository = AuthRepository()

        val registerResult = authRepository.register(
            RegisterRequest(
                email = "commenter@example.com",
                username = "commenter",
                password = "password123",
                bio = null
            )
        )
        assertTrue(registerResult.isSuccess)
        userId = registerResult.getOrNull()!!.user.id

        val post = postRepository.createPost(userId, "Post for comments", null)
        postId = post.id
    }

    @Test
    fun `createComment with valid data returns Comment`() {
        val comment = commentRepository.createComment(
            userId = userId,
            postId = postId,
            text = "Nice post!"
        )

        assertNotNull(comment)
        assertTrue(comment.id.isNotEmpty())
        assertEquals(postId, comment.postId)
        assertEquals(userId, comment.userId)
        assertEquals("Nice post!", comment.text)
        assertEquals(0, comment.likes)
    }

    @Test
    fun `getCommentById returns the correct comment`() {
        val created = commentRepository.createComment(userId, postId, "Find me")

        val found = commentRepository.getCommentById(created.id)

        assertNotNull(found)
        assertEquals(created.id, found.id)
        assertEquals("Find me", found.text)
    }

    @Test
    fun `getCommentById returns null for non-existent id`() {
        val found = commentRepository.getCommentById("non-existent-id")

        assertEquals(null, found)
    }

    @Test
    fun `getCommentsByPostId returns empty list when no comments exist`() {
        val comments = commentRepository.getCommentsByPostId(postId)

        assertTrue(comments.isEmpty())
    }

    @Test
    fun `getCommentsByPostId returns all comments for post`() {
        commentRepository.createComment(userId, postId, "First")
        commentRepository.createComment(userId, postId, "Second")
        commentRepository.createComment(userId, postId, "Third")

        val comments = commentRepository.getCommentsByPostId(postId)

        assertEquals(3, comments.size)
    }

    @Test
    fun `getCommentsByPostId only returns comments for the given post`() {
        val otherPost = postRepository.createPost(userId, "Other post", null)

        commentRepository.createComment(userId, postId, "For post A")
        commentRepository.createComment(userId, otherPost.id, "For post B")

        val postAComments = commentRepository.getCommentsByPostId(postId)
        val postBComments = commentRepository.getCommentsByPostId(otherPost.id)

        assertEquals(1, postAComments.size)
        assertEquals(1, postBComments.size)
        assertEquals("For post A", postAComments[0].text)
        assertEquals("For post B", postBComments[0].text)
    }

    @Test
    fun `createComment with non-existent post throws`() {
        var threw = false
        try {
            commentRepository.createComment(
                userId = userId,
                postId = "non-existent-post",
                text = "Orphan comment"
            )
        } catch (e: Exception) {
            threw = true
        }
        assertTrue(threw, "Expected FK violation but no exception was thrown")
    }

    @Test
    fun `createComment with non-existent user throws`() {
        var threw = false
        try {
            commentRepository.createComment(
                userId = "non-existent-user",
                postId = postId,
                text = "Ghost comment"
            )
        } catch (e: Exception) {
            threw = true
        }
        assertTrue(threw, "Expected FK violation but no exception was thrown")
    }

    @Test
    fun `likeComment creates a comment like`() {
        val comment = commentRepository.createComment(userId, postId, "Like me")

        val count = commentRepository.likeComment(comment.id, userId)

        assertEquals(1, count)
        assertTrue(commentRepository.isLikedBy(comment.id, userId))
    }

    @Test
    fun `likeComment is idempotent for same user`() {
        val comment = commentRepository.createComment(userId, postId, "Like me twice")

        commentRepository.likeComment(comment.id, userId)
        val count = commentRepository.likeComment(comment.id, userId)

        assertEquals(1, count)
    }

    @Test
    fun `unlikeComment removes the comment like`() {
        val comment = commentRepository.createComment(userId, postId, "Unlike me")
        commentRepository.likeComment(comment.id, userId)

        val count = commentRepository.unlikeComment(comment.id, userId)

        assertEquals(0, count)
        assertFalse(commentRepository.isLikedBy(comment.id, userId))
    }

    @Test
    fun `getLikeCount returns zero when no likes exist`() {
        val comment = commentRepository.createComment(userId, postId, "No likes")

        val count = commentRepository.getLikeCount(comment.id)

        assertEquals(0, count)
    }

    @Test
    fun `getLikeCount returns correct count after multiple likes`() {
        val otherUserResult = authRepository.register(
            RegisterRequest(
                email = "other@example.com",
                username = "otherliker",
                password = "password123",
                bio = null
            )
        )
        val otherUserId = otherUserResult.getOrNull()!!.user.id

        val comment = commentRepository.createComment(userId, postId, "Popular")

        commentRepository.likeComment(comment.id, userId)
        commentRepository.likeComment(comment.id, otherUserId)

        assertEquals(2, commentRepository.getLikeCount(comment.id))
    }
}