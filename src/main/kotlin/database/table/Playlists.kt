package com.example.database.table

import org.jetbrains.exposed.v1.core.Table

object Playlists : Table("playlists") {

    val id = uuid("id")
    val title = text("title")
    val genre = text("genre")
    val mood = text("mood")
    val albumImageUrl = text("albumImageUrl").nullable()
    val weather = text("weather")
    val temperature = text("temperature")
    val location = text("location")
    val likes = integer("likes").default(0)

    val spotifyUrl = text("spotifyUrl").nullable()
    val youtubeUrl = text("youtubeUrl").nullable()


    override val primaryKey = PrimaryKey(id)
}