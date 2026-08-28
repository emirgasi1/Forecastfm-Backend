package com.example.database

import com.example.database.seed.SeedData
import com.example.database.table.Comments
import com.example.database.table.FavoritePlaylists
import com.example.database.table.MusicHistory
import com.example.database.table.Musics
import com.example.database.table.PlaylistSongs
import com.example.database.table.Playlists
import com.example.database.table.PostLikes
import com.example.database.table.Posts
import com.example.database.table.SavedPosts
import com.example.database.table.Users
import com.example.music.MusicRepository
import com.example.playlist.PlaylistRepository
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.sql.DriverManager

object DatabaseFactory {

    fun init() {
        val url = System.getenv("DB_URL")
        val user = System.getenv("DB_USER")
        val password = System.getenv("DB_PASSWORD")

        Database.connect(
            url = url,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )

        transaction {
            SchemaUtils.create(
                Users,
                Posts,
                Comments,
                Musics,
                Playlists,
                PlaylistSongs,
                PostLikes,
                SavedPosts,
                FavoritePlaylists,
                MusicHistory
            )
        }


        DriverManager.getConnection(url, user, password).use { connection ->
            connection.createStatement().use { statement ->
                statement.executeQuery("SELECT 1").use { result ->
                    if (result.next() && result.getInt(1) == 1) {
                        println("PostgreSQL connection successful!")
                    }
                }
            }
        }
        val musicRepository = MusicRepository()
        val playlistRepository = PlaylistRepository()

        SeedData.seed(
            musicRepository = musicRepository,
            playlistRepository = playlistRepository
        )
    }
}