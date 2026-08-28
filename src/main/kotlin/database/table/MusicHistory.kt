package com.example.database.table

import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object MusicHistory : Table("music_history") {

    val id = uuid("id")

    val userId = uuid("userId")
        .references(Users.id)

    val playlistId = uuid("playlistId")
        .references(Playlists.id)

    val playedAt = timestamp("playedAt")

    override val primaryKey = PrimaryKey(id)
}