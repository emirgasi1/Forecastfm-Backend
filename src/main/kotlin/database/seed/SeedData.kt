package com.example.database.seed

import com.example.database.table.Locations
import com.example.database.table.Musics
import com.example.database.table.PlaylistSongs
import com.example.database.table.Playlists
import com.example.database.table.Posts
import com.example.music.MusicRepository
import com.example.playlist.PlaylistRepository
import database.table.Users
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.UUID
import kotlin.uuid.Uuid
import java.time.Instant

object SeedData {
    fun seedLocations() {
        transaction {
            // Only seed if locations are empty
            if (Locations.selectAll().count() == 0L) {

                // ===== SARAJEVO LOCATIONS =====

                // 1. Baščaršija
                val locationId1 = UUID.randomUUID().toString()
                Locations.insert {
                    it[Locations.id] = locationId1
                    it[Locations.name] = "Baščaršija"
                    it[Locations.description] = "Stari gradski trg, srce Sarajeva sa osmanskim duhom, ćevapima i bakrom"
                    it[Locations.latitude] = 43.8608
                    it[Locations.longitude] = 18.4288
                }

                // 2. Vrelo Bosne - Ilidža
                val locationId2 = UUID.randomUUID().toString()
                Locations.insert {
                    it[Locations.id] = locationId2
                    it[Locations.name] = "Vrelo Bosne"
                    it[Locations.description] = "Prirodni park na izvoru rijeke Bosne, idealan za šetnju i odmor"
                    it[Locations.latitude] = 43.8196
                    it[Locations.longitude] = 18.2695
                }

                // 3. Avaz Twist Tower
                val locationId3 = UUID.randomUUID().toString()
                Locations.insert {
                    it[Locations.id] = locationId3
                    it[Locations.name] = "Avaz Twist Tower"
                    it[Locations.description] = "Najviša zgrada u Bosni, pogled na cijelo Sarajevo sa 176m visine"
                    it[Locations.latitude] = 43.8584
                    it[Locations.longitude] = 18.4055
                }

                // 4. Bijela Tabija
                val locationId4 = UUID.randomUUID().toString()
                Locations.insert {
                    it[Locations.id] = locationId4
                    it[Locations.name] = "Bijela Tabija"
                    it[Locations.description] = "Stara osmanska tvrđava na brdu iznad Sarajeva, najbolji pogled na grad"
                    it[Locations.latitude] = 43.8620
                    it[Locations.longitude] = 18.4462
                }

                // 5. Katedrala Srca Isusova
                val locationId5 = UUID.randomUUID().toString()
                Locations.insert {
                    it[Locations.id] = locationId5
                    it[Locations.name] = "Katedrala Srca Isusova"
                    it[Locations.description] = "Neogotička katedrala u centru Sarajeva, simbol grada"
                    it[Locations.latitude] = 43.8593
                    it[Locations.longitude] = 18.4248
                }

                // 6. Sebilj
                val locationId6 = UUID.randomUUID().toString()
                Locations.insert {
                    it[Locations.id] = locationId6
                    it[Locations.name] = "Sebilj - Baščaršija"
                    it[Locations.description] = "Drvena česma iz 18. vijeka, najpoznatiji simbol Sarajeva"
                    it[Locations.latitude] = 43.8607
                    it[Locations.longitude] = 18.4290
                }

                // 7. Zmajevac
                val locationId7 = UUID.randomUUID().toString()
                Locations.insert {
                    it[Locations.id] = locationId7
                    it[Locations.name] = "Zmajevac"
                    it[Locations.description] = "Popularno izletište sa pogledom na Sarajevo, omiljeno mjesto za kafu"
                    it[Locations.latitude] = 43.8672
                    it[Locations.longitude] = 18.4122
                }

                // 8. Vijećnica
                val locationId8 = UUID.randomUUID().toString()
                Locations.insert {
                    it[Locations.id] = locationId8
                    it[Locations.name] = "Vijećnica"
                    it[Locations.description] = "Pseudo-maurska palača, nekadašnja gradska vijećnica, simbol Sarajeva"
                    it[Locations.latitude] = 43.8595
                    it[Locations.longitude] = 18.4331
                }

                // 9. Trebević
                val locationId9 = UUID.randomUUID().toString()
                Locations.insert {
                    it[Locations.id] = locationId9
                    it[Locations.name] = "Trebević - Vidikovac"
                    it[Locations.description] = "Planina iznad Sarajeva, uživajte u prirodi i panoramskom pogledu"
                    it[Locations.latitude] = 43.8271
                    it[Locations.longitude] = 18.4485
                }

                // 10. Ilidža - Veliki park
                val locationId10 = UUID.randomUUID().toString()
                Locations.insert {
                    it[Locations.id] = locationId10
                    it[Locations.name] = "Veliki park - Ilidža"
                    it[Locations.description] = "Prostrani park u Ilidži, savršen za opuštanje i porodične izlete"
                    it[Locations.latitude] = 43.8297
                    it[Locations.longitude] = 18.3132
                }

                // ===== SEED TEST USER =====
                val userId = UUID.randomUUID().toString()
                val hashedPassword = BCrypt.hashpw("password123", BCrypt.gensalt())

                Users.insert {
                    it[Users.id] = userId
                    it[Users.email] = "test@example.com"
                    it[Users.username] = "testuser"
                    it[Users.passwordHash] = hashedPassword
                    it[Users.bio] = "Istražujem Sarajevo! 🇧🇦"
                    it[Users.profileImageUrl] = null
                    it[Users.favoriteLocation] = "Baščaršija"
                    it[Users.isVerified] = true
                    it[Users.status] = "ACTIVE"
                }

                // ===== SEED TEST POST =====
                val postId = UUID.randomUUID().toString()
                Posts.insert {
                    it[Posts.id] = postId
                    it[Posts.userId] = userId
                    it[Posts.caption] = "Prvi put u Baščaršiji! Nevjerovatna atmosfera i najbolji ćevapi! 🌟 #ForecastFM #Sarajevo"
                    it[Posts.imageUrl] = null
                    it[Posts.createdAt] = Instant.now()
                }

                // ===== SEED TEST PLAYLIST =====
                val playlistId = UUID.randomUUID().toString()
                Playlists.insert {
                    it[Playlists.id] = playlistId
                    it[Playlists.title] = "Sarajevske Večeri"
                    it[Playlists.genre] = "Sevdah"
                    it[Playlists.mood] = "Romantično"
                    it[Playlists.albumImageUrl] = null
                    it[Playlists.weather] = "Sunčano"
                    it[Playlists.temperature] = "24°C"
                    it[Playlists.location] = "Baščaršija"
                    it[Playlists.likes] = 0
                    it[Playlists.spotifyUrl] = "https://open.spotify.com/playlist/37i9dQZF1DX4sWSpwq3LiO"
                    it[Playlists.youtubeUrl] = null
                }

                // ===== SEED TEST MUSIC =====
                val musicId = UUID.randomUUID().toString()
                Musics.insert {
                    it[Musics.id] = musicId
                    it[Musics.title] = "Sarajevo, volim te"
                    it[Musics.artist] = "Halid Bešlić"
                    it[Musics.duration] = 245
                    it[Musics.albumImageUrl] = null
                }

                // ===== ADD SONG TO PLAYLIST =====
                PlaylistSongs.insert {
                    it[PlaylistSongs.playlistId] = playlistId
                    it[PlaylistSongs.musicId] = musicId
                }

                println("✅ Seed data inserted successfully!")
                println("📍 10 Sarajevo locations seeded:")
                println("   - Baščaršija")
                println("   - Vrelo Bosne (Ilidža)")
                println("   - Avaz Twist Tower")
                println("   - Bijela Tabija")
                println("   - Katedrala Srca Isusova")
                println("   - Sebilj")
                println("   - Zmajevac")
                println("   - Vijećnica")
                println("   - Trebević")
                println("   - Veliki park (Ilidža)")
                println("👤 1 User seeded (test@example.com / password123)")
                println("📝 1 Post seeded")
                println("🎵 1 Playlist seeded")
                println("🎵 1 Music track seeded")
            }
        }
    }
}