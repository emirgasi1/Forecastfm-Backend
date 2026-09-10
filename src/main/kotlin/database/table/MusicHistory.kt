package com.example.database.table

import database.table.Users
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.javatime.timestamp

object MusicHistory : Table("music_history") {
    val id = text("id")
    val userId = text("userId").references(Users.id)
    val playlistId = text("playlistId").references(Playlists.id)
    val playedAt = timestamp("playedAt")

    override val primaryKey = PrimaryKey(id)
}