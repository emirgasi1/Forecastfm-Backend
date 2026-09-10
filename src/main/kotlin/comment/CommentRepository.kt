package com.example.comment

import database.table.Comments
import database.table.Comments.createdAt
import database.table.Comments.postId
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import java.util.UUID
import kotlin.uuid.Uuid

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
                        likes = it[Comments.likes]
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
                        likes = it[Comments.likes]
                    )
                }
        }
    }
}