package com.example.playlist

import com.example.database.table.FavoritePlaylists
import com.example.database.table.Musics
import com.example.database.table.PlaylistSongs
import com.example.database.table.Playlists
import com.example.music.Music
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import playlist.Playlist
import java.util.UUID

class PlaylistRepository {

    fun createPlaylist(
        title: String,
        genre: String,
        mood: String,
        albumImageUrl: String?,
        weather: String,
        temperature: String,
        location: String,
        spotifyUrl: String?,
        youtubeUrl: String?,
        bestFor: List<String> = emptyList()
    ): Playlist {
        val id = UUID.randomUUID().toString()

        transaction {
            Playlists.insert {
                it[Playlists.id] = id
                it[Playlists.title] = title
                it[Playlists.genre] = genre
                it[Playlists.mood] = mood
                it[Playlists.albumImageUrl] = albumImageUrl
                it[Playlists.weather] = weather
                it[Playlists.temperature] = temperature
                it[Playlists.location] = location
                it[Playlists.spotifyUrl] = spotifyUrl
                it[Playlists.youtubeUrl] = youtubeUrl
                it[Playlists.bestFor] = bestFor.joinToString(",")
            }
        }

        return Playlist(
            id = id,
            title = title,
            genre = genre,
            mood = mood,
            albumImageUrl = albumImageUrl,
            weather = weather,
            temperature = temperature,
            location = location,
            likes = 0,
            spotifyUrl = spotifyUrl,
            youtubeUrl = youtubeUrl,
            bestFor = bestFor
        )
    }

    fun getPlaylists(): List<PlaylistResponse> {
        return transaction {
            Playlists
                .selectAll()
                .map { playlist ->
                    PlaylistResponse(
                        id = playlist[Playlists.id],
                        title = playlist[Playlists.title],
                        genre = playlist[Playlists.genre],
                        mood = playlist[Playlists.mood],
                        albumImageUrl = playlist[Playlists.albumImageUrl],
                        weather = playlist[Playlists.weather],
                        temperature = playlist[Playlists.temperature],
                        location = playlist[Playlists.location],
                        songs = getSongsForPlaylist(playlist[Playlists.id]),
                        likes = playlist[Playlists.likes],
                        spotifyUrl = playlist[Playlists.spotifyUrl],
                        youtubeUrl = playlist[Playlists.youtubeUrl],
                        bestFor = playlist[Playlists.bestFor]
                            ?.split(",")
                            ?.filter { it.isNotBlank() }
                            ?: emptyList()
                    )
                }
        }
    }

    fun getPlaylistById(id: String): PlaylistResponse? {
        return transaction {
            Playlists
                .selectAll()
                .where { Playlists.id eq id }
                .singleOrNull()
                ?.let {
                    val songs = getSongsForPlaylist(id)
                    PlaylistResponse(
                        id = it[Playlists.id],
                        title = it[Playlists.title],
                        genre = it[Playlists.genre],
                        mood = it[Playlists.mood],
                        albumImageUrl = it[Playlists.albumImageUrl],
                        weather = it[Playlists.weather],
                        temperature = it[Playlists.temperature],
                        location = it[Playlists.location],
                        songs = songs,
                        likes = it[Playlists.likes],
                        spotifyUrl = it[Playlists.spotifyUrl],
                        youtubeUrl = it[Playlists.youtubeUrl],
                        bestFor = it[Playlists.bestFor]
                            ?.split(",")
                            ?.filter { it.isNotBlank() }
                            ?: emptyList()
                    )
                }
        }
    }

    fun addSongToPlaylist(
        playlistId: String,
        musicId: String
    ) {
        transaction {
            PlaylistSongs.insert {
                it[PlaylistSongs.playlistId] = playlistId
                it[PlaylistSongs.musicId] = musicId
            }
        }
    }

    fun getSongsForPlaylist(
        playlistId: String
    ): List<Music> {
        return transaction {
            (PlaylistSongs innerJoin Musics)
                .selectAll()
                .where { PlaylistSongs.playlistId eq playlistId }
                .map {
                    Music(
                        id = it[Musics.id],
                        title = it[Musics.title],
                        artist = it[Musics.artist],
                        duration = it[Musics.duration],
                        albumImageUrl = it[Musics.albumImageUrl]
                    )
                }
        }
    }

    fun getFavoritePlaylists(
        userId: String
    ): List<PlaylistResponse> {
        return transaction {
            (FavoritePlaylists innerJoin Playlists)
                .selectAll()
                .where {
                    FavoritePlaylists.userId eq userId
                }
                .map { row ->
                    val playlistId = row[Playlists.id]
                    PlaylistResponse(
                        id = playlistId,
                        title = row[Playlists.title],
                        genre = row[Playlists.genre],
                        mood = row[Playlists.mood],
                        albumImageUrl = row[Playlists.albumImageUrl],
                        weather = row[Playlists.weather],
                        temperature = row[Playlists.temperature],
                        location = row[Playlists.location],
                        songs = getSongsForPlaylist(playlistId),
                        likes = row[Playlists.likes],
                        spotifyUrl = row[Playlists.spotifyUrl],
                        youtubeUrl = row[Playlists.youtubeUrl],
                        bestFor = row[Playlists.bestFor]
                            ?.split(",")
                            ?.filter { it.isNotBlank() }
                            ?: emptyList()
                    )
                }
        }
    }

    fun getFavoritePlaylistIds(
        userId: String
    ): List<String> {
        return transaction {
            FavoritePlaylists
                .selectAll()
                .where {
                    FavoritePlaylists.userId eq userId
                }
                .map {
                    it[FavoritePlaylists.playlistId]
                }
        }
    }

    fun favoritePlaylist(
        userId: String,
        playlistId: String
    ) {
        transaction {
            FavoritePlaylists.insert {
                it[FavoritePlaylists.userId] = userId
                it[FavoritePlaylists.playlistId] = playlistId
            }
        }
    }

    fun unfavoritePlaylist(
        userId: String,
        playlistId: String
    ) {
        transaction {
            FavoritePlaylists.deleteWhere {
                (FavoritePlaylists.userId eq userId) and
                        (FavoritePlaylists.playlistId eq playlistId)
            }
        }
    }
    fun updatePlaylistImage(
        playlistId: String,
        imageUrl: String
    ) {
        transaction {
            Playlists.update({ Playlists.id eq playlistId }) {
                it[Playlists.albumImageUrl] = imageUrl
            }
        }
    }
}