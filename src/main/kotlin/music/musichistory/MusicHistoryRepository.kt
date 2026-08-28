package com.example.music

import com.example.database.table.MusicHistory
import com.example.database.table.Playlists
import com.example.music.musichistory.MusicHistoryEntry
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.time.Instant
import kotlin.uuid.Uuid

class MusicHistoryRepository {

    fun addHistory(
        userId: Uuid,
        playlistId: Uuid
    ) {

        val id = Uuid.random()

        transaction {
            MusicHistory.insert {
                it[MusicHistory.id] = id
                it[MusicHistory.userId] = userId
                it[MusicHistory.playlistId] = playlistId
                it[MusicHistory.playedAt] = Instant.now()
            }
        }
    }

    fun getHistory(
        userId: Uuid
    ): List<MusicHistoryEntry> {

        return transaction {
            (MusicHistory innerJoin Playlists)
                .selectAll()
                .where {
                    MusicHistory.userId eq userId
                }
                .map {
                    MusicHistoryEntry(
                        id = it[MusicHistory.id].toString(),
                        playlistId = it[MusicHistory.playlistId].toString(),
                        title = it[Playlists.title],
                        weather = it[Playlists.weather],
                        temperature = it[Playlists.temperature],
                        location = it[Playlists.location],
                        playedAt = it[MusicHistory.playedAt].toString()
                    )
                }
        }
    }


}