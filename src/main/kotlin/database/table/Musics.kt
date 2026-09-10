package com.example.database.table

import org.jetbrains.exposed.v1.core.Table

object Musics : Table("musics") {
    val id = text("id")
    val title = varchar("title", 255)
    val artist = varchar("artist", 255)
    val duration = integer("duration")
    val albumImageUrl = text("albumImageUrl").nullable()

    override val primaryKey = PrimaryKey(id)
}