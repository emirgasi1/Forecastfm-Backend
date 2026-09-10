package com.example.music

import com.example.database.table.Musics
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import kotlin.uuid.Uuid

class MusicRepository {

    fun createMusic(
        title: String,
        artist: String,
        duration: Int,  // Changed from String to Int
        albumImageUrl: String?
    ): Music {
        val id = UUID.randomUUID().toString()

        transaction {
            Musics.insert {
                it[Musics.id] = id
                it[Musics.title] = title
                it[Musics.artist] = artist
                it[Musics.duration] = duration  // Now Int matches
                it[Musics.albumImageUrl] = albumImageUrl
            }
        }

        return Music(
            id = id,
            title = title,
            artist = artist,
            duration = duration,
            albumImageUrl = albumImageUrl
        )
    }

    fun getMusicById(id: String): Music? {
        return transaction {
            Musics
                .selectAll()
                .where { Musics.id eq id }
                .map {
                    Music(
                        id = it[Musics.id],
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