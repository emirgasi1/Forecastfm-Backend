package com.example.post
import com.example.database.table.Posts
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.time.Instant
import java.util.UUID
import kotlin.uuid.Uuid

class PostRepository {

    fun createPost(
        userId: String,
        caption: String?,
        imageUrl: String?
    ): Post {
        val id = UUID.randomUUID().toString()
        val createdAt = Instant.now()

        transaction {
            Posts.insert {
                it[Posts.id] = id
                it[Posts.userId] = userId
                it[Posts.caption] = caption
                it[Posts.imageUrl] = imageUrl
                it[Posts.createdAt] = createdAt
            }
        }

        return Post(
            id = id,
            userId = userId,
            caption = caption,
            imageUrl = imageUrl,
            createdAt = createdAt.toString()
        )
    }

    fun getPosts(): List<Post> {
        return transaction {
            Posts
                .selectAll()
                .map {
                    Post(
                        id = it[Posts.id],
                        userId = it[Posts.userId],
                        caption = it[Posts.caption],
                        imageUrl = it[Posts.imageUrl],
                        createdAt = it[Posts.createdAt].toString()
                    )
                }
        }
    }

    fun getPostById(id: String): Post? {
        return transaction {
            Posts
                .selectAll()
                .where { Posts.id eq id }
                .map {
                    Post(
                        id = it[Posts.id],
                        userId = it[Posts.userId],
                        caption = it[Posts.caption],
                        imageUrl = it[Posts.imageUrl],
                        createdAt = it[Posts.createdAt].toString()
                    )
                }
                .singleOrNull()
        }
    }

    fun getPostsByUserId(userId: String): List<Post> {
        return transaction {
            Posts
                .selectAll()
                .where { Posts.userId eq userId }
                .map {
                    Post(
                        id = it[Posts.id],
                        userId = it[Posts.userId],
                        caption = it[Posts.caption],
                        imageUrl = it[Posts.imageUrl],
                        createdAt = it[Posts.createdAt].toString()
                    )
                }
        }
    }

    fun updatePostImage(postId: String, imageUrl: String): Post {
        transaction {
            Posts.update({ Posts.id eq postId }) {
                it[Posts.imageUrl] = imageUrl
            }
        }
        return getPostById(postId)!!
    }
}