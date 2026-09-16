package com.example.comment

import com.example.database.table.CommentLikes
import database.table.Comments
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.util.UUID

class CommentRepository {

    fun createComment(
        userId: String,
        postId: String,
        text: String
    ): Comment {
        val id = UUID.randomUUID().toString()
        val createdAt = Instant.now()

        transaction {
            Comments.insert {
                it[Comments.id] = id
                it[Comments.userId] = userId
                it[Comments.postId] = postId
                it[Comments.text] = text
                it[Comments.createdAt] = createdAt
            }
        }

        return Comment(
            id = id,
            userId = userId,
            postId = postId,
            text = text,
            createdAt = createdAt.toString(),
            likes = 0
        )
    }

    fun getCommentById(id: String): Comment? {
        return transaction {
            Comments
                .selectAll()
                .where { Comments.id eq id }
                .singleOrNull()
                ?.let {
                    Comment(
                        id = it[Comments.id],
                        userId = it[Comments.userId],
                        postId = it[Comments.postId],
                        text = it[Comments.text],
                        createdAt = it[Comments.createdAt].toString(),
                        likes = getLikeCount(it[Comments.id])
                    )
                }
        }
    }

    fun getCommentsByPostId(postId: String): List<Comment> {
        return transaction {
            Comments
                .selectAll()
                .where { Comments.postId eq postId }
                .map {
                    Comment(
                        id = it[Comments.id],
                        userId = it[Comments.userId],
                        postId = it[Comments.postId],
                        text = it[Comments.text],
                        createdAt = it[Comments.createdAt].toString(),
                        likes = getLikeCount(it[Comments.id])
                    )
                }
        }
    }

    fun getLikeCount(commentId: String): Int {
        return transaction {
            CommentLikes
                .selectAll()
                .where { CommentLikes.commentId eq commentId }
                .count()
                .toInt()
        }
    }

    fun isLikedBy(commentId: String, userId: String): Boolean {
        return transaction {
            CommentLikes
                .selectAll()
                .where {
                    (CommentLikes.commentId eq commentId) and
                            (CommentLikes.userId eq userId)
                }
                .count() > 0
        }
    }

    fun likeComment(commentId: String, userId: String): Int {
        return transaction {
            val exists = CommentLikes
                .selectAll()
                .where {
                    (CommentLikes.commentId eq commentId) and
                            (CommentLikes.userId eq userId)
                }
                .count() > 0

            if (!exists) {
                CommentLikes.insert {
                    it[CommentLikes.id] = UUID.randomUUID().toString()
                    it[CommentLikes.commentId] = commentId
                    it[CommentLikes.userId] = userId
                    it[CommentLikes.createdAt] = Instant.now()
                }
            }

            CommentLikes
                .selectAll()
                .where { CommentLikes.commentId eq commentId }
                .count()
                .toInt()
        }
    }

    fun unlikeComment(commentId: String, userId: String): Int {
        return transaction {
            CommentLikes.deleteWhere {
                (CommentLikes.commentId eq commentId) and
                        (CommentLikes.userId eq userId)
            }

            CommentLikes
                .selectAll()
                .where { CommentLikes.commentId eq commentId }
                .count()
                .toInt()
        }
    }
}