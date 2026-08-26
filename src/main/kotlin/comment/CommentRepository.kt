package com.example.comment

import com.example.database.table.Comments
import com.example.database.table.Comments.createdAt
import com.example.database.table.Comments.postId
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import kotlin.uuid.Uuid

class CommentRepository {
    fun createComment(
        userId: Uuid,
        postId: Uuid,
        text: String
    ): Comment {

        val id = Uuid.random()
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
            id = id.toString(),
            userId = userId.toString(),
            postId = postId.toString(),
            text = text,
            createdAt = createdAt.toString(),
            likes = 0
        )
    }

    fun getCommentById(id: Uuid): Comment? {

        return transaction {
            Comments
                .selectAll()
                .where { Comments.id eq id }
                .singleOrNull()
                ?.let {
                    Comment(
                        id = it[Comments.id].toString(),
                        userId = it[Comments.userId].toString(),
                        postId = it[Comments.postId].toString(),
                        text = it[Comments.text],
                        createdAt = it[Comments.createdAt].toString(),
                        likes = it[Comments.likes]
                    )
                }
        }
    }

    fun getCommentsByPostId(postId: Uuid): List<Comment> {

        return transaction {

            Comments
                .selectAll()
                .where { Comments.postId eq postId }
                .map {
                    Comment(
                        id = it[Comments.id].toString(),
                        userId = it[Comments.userId].toString(),
                        postId = it[Comments.postId].toString(),
                        text = it[Comments.text],
                        createdAt = it[Comments.createdAt].toString(),
                        likes = it[Comments.likes]
                    )
                }
        }
    }
}