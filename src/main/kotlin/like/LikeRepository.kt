package com.example.like

import com.example.database.table.PostLikes
import com.example.database.table.Posts
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.Uuid

class LikeRepository {

    fun likePost(
        userId: String,
        postId: String
    ) {
        transaction {
            PostLikes.insert {
                it[PostLikes.userId] = userId
                it[PostLikes.postId] = postId
            }
        }
    }

    fun unlikePost(
        userId: String,
        postId: String
    ) {
        transaction {
            PostLikes.deleteWhere {
                (PostLikes.userId eq userId) and
                        (PostLikes.postId eq postId)
            }
        }
    }

    fun isPostLiked(
        userId: String,
        postId: String
    ): Boolean {
        return transaction {
            PostLikes
                .selectAll()
                .where {
                    (PostLikes.userId eq userId) and
                            (PostLikes.postId eq postId)
                }
                .any()
        }
    }

    fun getPostLikeCount(
        postId: String
    ): Int {
        return transaction {
            PostLikes
                .selectAll()
                .where { PostLikes.postId eq postId }
                .count()
                .toInt()
        }
    }

    fun getUserReceivedLikeCount(
        userId: String
    ): Int {
        return transaction {
            PostLikes
                .innerJoin(Posts)
                .selectAll()
                .where {
                    Posts.userId eq userId
                }
                .count()
                .toInt()
        }
    }
}