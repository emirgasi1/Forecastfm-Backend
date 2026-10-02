package com.example.post

import com.example.database.table.Outfits
import com.example.database.table.Posts
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import java.time.Instant
import java.util.UUID

class PostRepository {

    fun createPost(
        userId: String,
        caption: String?,
        imageUrl: String?,
        outfitId: String? = null
    ): Post {
        val id = UUID.randomUUID().toString()
        val createdAt = Instant.now()

        // Resolve outfit title at write time. If the outfit doesn't exist,
        // store the id but no title — card will render nothing.
        val outfitTitle: String? = outfitId?.let { oid ->
            transaction {
                Outfits
                    .selectAll()
                    .where { Outfits.id eq oid }
                    .map { it[Outfits.title] }
                    .singleOrNull()
            }
        }

        transaction {
            Posts.insert {
                it[Posts.id] = id
                it[Posts.userId] = userId
                it[Posts.caption] = caption
                it[Posts.imageUrl] = imageUrl
                it[Posts.outfitId] = outfitId
                it[Posts.outfitTitle] = outfitTitle
                it[Posts.createdAt] = createdAt
            }
        }

        return Post(
            id = id,
            userId = userId,
            caption = caption,
            imageUrl = imageUrl,
            outfitId = outfitId,
            outfitTitle = outfitTitle,
            createdAt = createdAt.toString()
        )
    }

    fun getPosts(): List<Post> = transaction {
        Posts.selectAll().map { it.toPost() }
    }

    fun getPostById(id: String): Post? = transaction {
        Posts
            .selectAll()
            .where { Posts.id eq id }
            .map { it.toPost() }
            .singleOrNull()
    }

    fun getPostsByUserId(userId: String): List<Post> = transaction {
        Posts
            .selectAll()
            .where { Posts.userId eq userId }
            .map { it.toPost() }
    }

    fun updatePostImage(postId: String, imageUrl: String): Post {
        transaction {
            Posts.update({ Posts.id eq postId }) {
                it[Posts.imageUrl] = imageUrl
            }
        }
        return getPostById(postId)!!
    }

    private fun org.jetbrains.exposed.v1.core.ResultRow.toPost() = Post(
        id = this[Posts.id],
        userId = this[Posts.userId],
        caption = this[Posts.caption],
        imageUrl = this[Posts.imageUrl],
        outfitId = this[Posts.outfitId],
        outfitTitle = this[Posts.outfitTitle],
        createdAt = this[Posts.createdAt].toString()
    )
}