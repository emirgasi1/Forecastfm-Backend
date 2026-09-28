package com.example

import com.example.database.table.CommentLikes
import com.example.database.table.FavoritePlaylists
import com.example.database.table.MusicHistory
import com.example.database.table.PostLikes
import com.example.database.table.Posts
import com.example.database.table.SavedOutfits
import com.example.database.table.SavedPlaces
import com.example.database.table.SavedPosts
import database.table.Comments
import database.table.UserSessions
import database.table.Users
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object TestDataCleaner {

    fun clearAll() {
        transaction {
            CommentLikes.deleteAll()
            Comments.deleteAll()
            PostLikes.deleteAll()
            SavedPosts.deleteAll()
            SavedPlaces.deleteAll()
            SavedOutfits.deleteAll()
            FavoritePlaylists.deleteAll()
            MusicHistory.deleteAll()
            Posts.deleteAll()
            UserSessions.deleteAll()
            Users.deleteAll()
        }
    }
}