package com.example.post
import com.example.database.table.Posts
import com.example.database.table.SavedPosts
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.Uuid

class SavedPostRepository {

    fun savePost(
        userId: String,
        postId: String
    ) {
        transaction {
            SavedPosts.insert {
                it[SavedPosts.userId] = userId
                it[SavedPosts.postId] = postId
            }
        }
    }

    fun unsavePost(
        userId: String,
        postId: String
    ) {
        transaction {
            SavedPosts.deleteWhere {
                (SavedPosts.userId eq userId) and
                        (SavedPosts.postId eq postId)
            }
        }
    }

    fun isPostSaved(
        userId: String,
        postId: String
    ): Boolean {
        return transaction {
            SavedPosts
                .selectAll()
                .where {
                    (SavedPosts.userId eq userId) and
                            (SavedPosts.postId eq postId)
                }
                .any()
        }
    }

    fun getSavedPosts(
        userId: String
    ): List<Post> {
        return transaction {
            (SavedPosts innerJoin Posts)
                .selectAll()
                .where { SavedPosts.userId eq userId }
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

    fun getSavedPostCount(
        userId: String
    ): Int {
        return transaction {
            SavedPosts
                .selectAll()
                .where {
                    SavedPosts.userId eq userId
                }
                .count()
                .toInt()
        }
    }
}