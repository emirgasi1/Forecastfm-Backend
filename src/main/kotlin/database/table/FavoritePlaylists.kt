package com.example.database.table

import org.jetbrains.exposed.v1.core.Table

object FavoritePlaylists : Table("favorite_playlists") {

    val userId = uuid("userId")
        .references(Users.id)

    val playlistId = uuid("playlistId")
        .references(Playlists.id)

    override val primaryKey = PrimaryKey(
        userId,
        playlistId
    )
}