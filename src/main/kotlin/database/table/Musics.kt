package com.example.database.table

import org.jetbrains.exposed.v1.core.Table

object Musics : Table("music") {

    val id = uuid("id")
    val title = text("title")
    val artist = text("artist")
    val duration = text("duration")
    val albumImageUrl = text("albumImageUrl").nullable()

    override val primaryKey = PrimaryKey(id)
}