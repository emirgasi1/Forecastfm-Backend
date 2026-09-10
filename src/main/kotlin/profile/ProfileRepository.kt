package com.example.profile

import com.example.like.LikeRepository
import com.example.playlist.PlaylistRepository
import com.example.post.PostRepository
import com.example.post.SavedPostRepository
import database.table.Users
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.Uuid

class ProfileRepository {
    private val postRepository = PostRepository()
    private val likeRepository = LikeRepository()
    private val savedPostRepository = SavedPostRepository()
    private val playlistRepository = PlaylistRepository()

    fun getProfile(userId: String): ProfileResponse {
        val user = transaction {
            Users
                .selectAll()
                .where { Users.id eq userId }
                .singleOrNull()
        } ?: throw IllegalArgumentException("User not found")

        val posts = postRepository.getPostsByUserId(userId)
        val likes = likeRepository.getUserReceivedLikeCount(userId)
        val saved = savedPostRepository.getSavedPostCount(userId)
        val favoritePlaylists = playlistRepository.getFavoritePlaylists(userId)

        return ProfileResponse(
            id = user[Users.id],
            username = user[Users.username],
            bio = user[Users.bio],
            profileImageUrl = user[Users.profileImageUrl],
            favoriteLocation = user[Users.favoriteLocation],
            likes = likes,
            saved = saved,
            posts = posts,
            favoritePlaylists = favoritePlaylists
        )
    }
}