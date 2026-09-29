package com.example

import com.example.database.table.BusStations
import com.example.database.table.CommentLikes
import com.example.database.table.FavoritePlaylists
import com.example.database.table.Locations
import com.example.database.table.MusicHistory
import com.example.database.table.Musics
import com.example.database.table.Outfits
import com.example.database.table.PlaceRecommendations
import com.example.database.table.Places
import com.example.database.table.Playlists
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
            BusStations.deleteAll()
            CommentLikes.deleteAll()
            Comments.deleteAll()
            PostLikes.deleteAll()
            SavedPosts.deleteAll()
            SavedPlaces.deleteAll()
            SavedOutfits.deleteAll()
            FavoritePlaylists.deleteAll()
            Outfits.deleteAll()
            SavedOutfits.deleteAll()
            MusicHistory.deleteAll()
            Musics.deleteAll()
            Posts.deleteAll()
            Playlists.deleteAll()
            Places.deleteAll()
            PlaceRecommendations.deleteAll()
            UserSessions.deleteAll()
            Users.deleteAll()
            Locations.deleteAll()
        }
    }
}