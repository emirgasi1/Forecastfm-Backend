package com.example.database.table

import database.table.Users
import org.jetbrains.exposed.v1.core.Table

object FavoritePlaylists : Table("favorite_playlists") {
    val userId = text("userId").references(Users.id)
    val playlistId = text("playlistId").references(Playlists.id)

    override val primaryKey = PrimaryKey(userId, playlistId)
}