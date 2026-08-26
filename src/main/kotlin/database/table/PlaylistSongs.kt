package com.example.database.table


import org.jetbrains.exposed.v1.core.Table

object PlaylistSongs : Table("playlist_songs") {

    val playlistId = uuid("playlistId")
        .references(Playlists.id)

    val musicId = uuid("musicId")
        .references(Musics.id)

    override val primaryKey = PrimaryKey(
        playlistId,
        musicId
    )
}