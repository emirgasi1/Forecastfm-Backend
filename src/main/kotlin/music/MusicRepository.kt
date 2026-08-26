package com.example.music

import com.example.database.table.Musics
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.Uuid

class MusicRepository {

    fun createMusic(
        title: String,
        artist: String,
        duration: String,
        albumImageUrl: String?
    ): Music {

        val id = Uuid.random()

        transaction {
            Musics.insert {
                it[Musics.id] = id
                it[Musics.title] = title
                it[Musics.artist] = artist
                it[Musics.duration] = duration
                it[Musics.albumImageUrl] = albumImageUrl
            }
        }

        return Music(
            id = id.toString(),
            title = title,
            artist = artist,
            duration = duration,
            albumImageUrl = albumImageUrl
        )
    }

    fun getMusicById(id: Uuid): Music? {

        return transaction {
            Musics
                .selectAll()
                .where { Musics.id eq id }
                .map {
                    Music(
                        id = it[Musics.id].toString(),
                        title = it[Musics.title],
                        artist = it[Musics.artist],
                        duration = it[Musics.duration],
                        albumImageUrl = it[Musics.albumImageUrl]
                    )
                }
                .singleOrNull()
        }
    }
}