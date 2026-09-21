package com.example.database.seed

import com.example.database.table.BusStations
import com.example.database.table.Locations
import com.example.database.table.Musics
import com.example.database.table.Outfits
import com.example.database.table.PlaceRecommendations
import com.example.database.table.Places
import com.example.database.table.PlaylistSongs
import com.example.database.table.Playlists
import com.example.database.table.Posts
import database.table.Users
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.time.Instant
import java.time.LocalDateTime

object SeedData {

    private const val SEED_USER_01 = "seed-user-amina"
    private const val SEED_USER_02 = "seed-user-adnan"
    private const val SEED_USER_03 = "seed-user-lejla"
    private const val SEED_USER_04 = "seed-user-dino"
    private const val SEED_USER_05 = "seed-user-sara"

    fun seedAll() {
        try {
            println("SeedAll starting")
            transaction {
                seedUsers()
                seedLocations()
                seedMusics()
                seedPlaylists()
                seedPlaylistSongs()
                seedOutfits()
                seedPlaces()
                seedBusStations()
                seedPosts()
            }
            println("Forecast FM seed data completed successfully.")
        } catch (e: Exception) {
            println("SEED FAILED: ${e::class.simpleName}: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun seedUsers() {
        val users = listOf(
            UserSeed(SEED_USER_01, "amina.kovac@forecastfm.demo", "amina_kovac", "Coffee, sunsets and Sarajevo walks.", "Baščaršija"),
            UserSeed(SEED_USER_02, "adnan.hadzic@forecastfm.demo", "adnan_hadzic", "Music, food and exploring new places.", "Marijin Dvor"),
            UserSeed(SEED_USER_03, "lejla.basic@forecastfm.demo", "lejla_basic", "Finding the perfect outfit for every weather.", "Ilidža"),
            UserSeed(SEED_USER_04, "dino.memisevic@forecastfm.demo", "dino_m", "Always looking for the next good playlist.", "Trebević"),
            UserSeed(SEED_USER_05, "sara.kovacevic@forecastfm.demo", "sara_k", "Sarajevo through my camera.", "Vijećnica")
        )

        users.forEach { user ->
            val exists = Users
                .select(Users.id)
                .where { Users.id eq user.id }
                .count() > 0

            if (!exists) {
                Users.insert {
                    it[Users.id] = user.id
                    it[Users.email] = user.email
                    it[Users.username] = user.username
                    it[Users.passwordHash] = BCrypt.hashpw("Forecast123!", BCrypt.gensalt())
                    it[Users.bio] = user.bio
                    it[Users.profileImageUrl] = null
                    it[Users.favoriteLocation] = user.favoriteLocation
                    it[Users.isVerified] = true
                    it[Users.createdAt] = LocalDateTime.now()
                    it[Users.lastLoginAt] = null
                    it[Users.status] = "ACTIVE"
                }
            }
        }

        println("Seeded demo users.")
    }

    private fun seedLocations() {
        val osmLocations = listOf(
            LocationSeed("osm-loc-aerodromsko-naselje", "Aerodromsko naselje", "Naselje u Sarajevu.", 43.8291397, 18.3386362),
            LocationSeed("osm-loc-ahatovici", "Ahatovići", "Naselje u Sarajevu.", 43.8846542, 18.2829095),
            LocationSeed("osm-loc-alipasin-most", "Alipašin most", "Naselje u Sarajevu.", 43.8486024, 18.3456776),
            LocationSeed("osm-loc-alipasino-polje", "Alipašino Polje", "Naselje u Sarajevu.", 43.8431072, 18.3478944),
            LocationSeed("osm-loc-aneks", "Aneks", "Naselje u Sarajevu.", 43.8452675, 18.3709967),
            LocationSeed("osm-loc-azici", "Azići", "Naselje u Sarajevu.", 43.8472394, 18.3136793),
            LocationSeed("osm-loc-babica-basca", "Babića bašča", "Naselje u Sarajevu.", 43.8573853, 18.4351632),
            LocationSeed("osm-loc-bare", "Bare", "Naselje u Sarajevu.", 43.875697, 18.4027942),
            LocationSeed("osm-loc-bascarsija", "Baščaršija", "Naselje u Sarajevu.", 43.8591032, 18.4309501),
            LocationSeed("osm-loc-bijelo-polje", "Bijelo Polje", "Naselje u Sarajevu.", 43.819639, 18.3530161),
            LocationSeed("osm-loc-bistrik", "Bistrik", "Naselje u Sarajevu.", 43.8550903, 18.4301122),
            LocationSeed("osm-loc-bjelave", "Bjelave", "Naselje u Sarajevu.", 43.8637854, 18.4195229),
            LocationSeed("osm-loc-blazuj", "Blažuj", "Naselje u Sarajevu.", 43.8401451, 18.258022),
            LocationSeed("osm-loc-boljakov-potok", "Boljakov Potok", "Naselje u Sarajevu.", 43.8562415, 18.3478016),
            LocationSeed("osm-loc-breka", "Breka", "Naselje u Sarajevu.", 43.8711996, 18.417406),
            LocationSeed("osm-loc-brijesce", "Briješće", "Naselje u Sarajevu.", 43.8614041, 18.3299047),
            LocationSeed("osm-loc-brijesce-polje", "Briješće polje", "Naselje u Sarajevu.", 43.8564713, 18.3109988),
            LocationSeed("osm-loc-butmir", "Butmir", "Naselje u Sarajevu.", 43.8190637, 18.3256681),
            LocationSeed("osm-loc-buca-potok", "Buća Potok", "Naselje u Sarajevu.", 43.8611585, 18.3585336),
            LocationSeed("osm-loc-cicin-han", "Cicin Han", "Naselje u Sarajevu.", 43.8501127, 18.419702),
            LocationSeed("osm-loc-ciglane", "Ciglane", "Naselje u Sarajevu.", 43.8645049, 18.408036),
            LocationSeed("osm-loc-crni-vrh", "Crni vrh", "Naselje u Sarajevu.", 43.8618287, 18.4071042),
            LocationSeed("osm-loc-dobrinja", "Dobrinja", "Naselje u Sarajevu.", 43.8291255, 18.3463543),
            LocationSeed("osm-loc-dobrinja-i", "Dobrinja I", "Naselje u Sarajevu.", 43.823389, 18.3522447),
            LocationSeed("osm-loc-dobrinja-ii", "Dobrinja II", "Naselje u Sarajevu.", 43.8279694, 18.3492282),
            LocationSeed("osm-loc-dobrinja-iii", "Dobrinja III", "Naselje u Sarajevu.", 43.8303246, 18.3510055),
            LocationSeed("osm-loc-dobrinja-iv", "Dobrinja IV", "Naselje u Sarajevu.", 43.8280287, 18.3583108),
            LocationSeed("osm-loc-dobrinja-v", "Dobrinja V", "Naselje u Sarajevu.", 43.8336463, 18.3433173),
            LocationSeed("osm-loc-dobrinjska-mahala", "Dobrinjska Mahala", "Naselje u Sarajevu.", 43.8316528, 18.3402436),
            LocationSeed("osm-loc-dobrosevici", "Dobroševići", "Naselje u Sarajevu.", 43.880004, 18.2897631),
            LocationSeed("osm-loc-doglodi", "Doglodi", "Naselje u Sarajevu.", 43.8534488, 18.295338),
            LocationSeed("osm-loc-dolac-malta", "Dolac Malta", "Naselje u Sarajevu.", 43.8533947, 18.3792689),
            LocationSeed("osm-loc-donja-josanica", "Donja Jošanica", "Naselje u Sarajevu.", 43.8956907, 18.3377699),
            LocationSeed("osm-loc-donje-mladice", "Donje Mladice", "Naselje u Sarajevu.", 43.8098648, 18.3633044),
            LocationSeed("osm-loc-donji-kotorac", "Donji Kotorac", "Naselje u Sarajevu.", 43.8177692, 18.3378372),
            LocationSeed("osm-loc-drvenija", "Drvenija", "Naselje u Sarajevu.", 43.8559992, 18.4230501),
            LocationSeed("osm-loc-dzidzikovac", "Džidžikovac", "Naselje u Sarajevu.", 43.8616456, 18.4163868),
            LocationSeed("osm-loc-gorica", "Gorica", "Naselje u Sarajevu.", 43.8635717, 18.4062616),
            LocationSeed("osm-loc-gornja-josanica", "Gornja Jošanica", "Naselje u Sarajevu.", 43.9073347, 18.3401415),
            LocationSeed("osm-loc-gornji-kotorac", "Gornji Kotorac", "Naselje u Sarajevu.", 43.8080843, 18.3547178),
            LocationSeed("osm-loc-gornji-velesici", "Gornji Velešići", "Naselje u Sarajevu.", 43.8701631, 18.3979098),
            LocationSeed("osm-loc-grbavica", "Grbavica", "Naselje u Sarajevu.", 43.8512613, 18.3984371),
            LocationSeed("osm-loc-hipodrom", "Hipodrom", "Naselje u Sarajevu.", 43.8073363, 18.3306921),
            LocationSeed("osm-loc-hodzin-otok", "Hodžin otok", "Naselje u Sarajevu.", 43.7999933, 18.3027785),
            LocationSeed("osm-loc-hrasno", "Hrasno", "Naselje u Sarajevu.", 43.8490518, 18.3835027),
            LocationSeed("osm-loc-hrasno-brdo", "Hrasno Brdo", "Naselje u Sarajevu.", 43.8442823, 18.3817432),
            LocationSeed("osm-loc-hrid", "Hrid", "Naselje u Sarajevu.", 43.85374, 18.4379446),
            LocationSeed("osm-loc-jarcedoli", "Jarčedoli", "Naselje u Sarajevu.", 43.8540572, 18.4491777),
            LocationSeed("osm-loc-jezero", "Jezero", "Naselje u Sarajevu.", 43.8762114, 18.4130708),
            LocationSeed("osm-loc-korea", "Korea", "Naselje u Sarajevu.", 43.8561386, 18.3850087),
            LocationSeed("osm-loc-kovaci", "Kovači", "Naselje u Sarajevu.", 43.862549, 18.4340373),
            LocationSeed("osm-loc-kovacici", "Kovačići", "Naselje u Sarajevu.", 43.8504988, 18.4037989),
            LocationSeed("osm-loc-kosevo", "Koševo", "Naselje u Sarajevu.", 43.8715709, 18.4134142),
            LocationSeed("osm-loc-kosevsko-brdo", "Koševsko brdo", "Naselje u Sarajevu.", 43.8679888, 18.4047143),
            LocationSeed("osm-loc-kula", "Kula", "Naselje u Sarajevu.", 43.8153388, 18.3547214),
            LocationSeed("osm-loc-lemezi", "Lemezi", "Naselje u Sarajevu.", 43.8704401, 18.317337),
            LocationSeed("osm-loc-lukavica", "Lukavica", "Naselje u Sarajevu.", 43.8388743, 18.37345),
            LocationSeed("osm-loc-luzani", "Lužani", "Naselje u Sarajevu.", 43.8288155, 18.2998328),
            LocationSeed("osm-loc-mahmutovac", "Mahmutovac", "Naselje u Sarajevu.", 43.8500653, 18.4345333),
            LocationSeed("osm-loc-marijin-dvor", "Marijin Dvor", "Naselje u Sarajevu.", 43.8559143, 18.408278),
            LocationSeed("osm-loc-medrese", "Medrese", "Naselje u Sarajevu.", 43.8675566, 18.4330819),
            LocationSeed("osm-loc-mejtas", "Mejtaš", "Naselje u Sarajevu.", 43.8612082, 18.4194456),
            LocationSeed("osm-loc-miljevici", "Miljevići", "Naselje u Sarajevu.", 43.8443118, 18.4032252),
            LocationSeed("osm-loc-mladicko-polje", "Mladičko Polje", "Naselje u Sarajevu.", 43.8075869, 18.3720762),
            LocationSeed("osm-loc-mojmilo", "Mojmilo", "Naselje u Sarajevu.", 43.8381724, 18.346157),
            LocationSeed("osm-loc-mojmilo-brdo", "Mojmilo brdo", "Naselje u Sarajevu.", 43.8395003, 18.3560312),
            LocationSeed("osm-loc-moscanica", "Mošćanica", "Naselje u Sarajevu.", 43.8681919, 18.449001),
            LocationSeed("osm-loc-naselje-bulevar", "Naselje Bulevar", "Naselje u Sarajevu.", 43.840292, 18.3230076),
            LocationSeed("osm-loc-naselje-starosjedilaca", "Naselje Starosjedilaca", "Naselje u Sarajevu.", 43.8308259, 18.3654286),
            LocationSeed("osm-loc-naselje-heroja-sokolje", "Naselje heroja Sokolje", "Naselje u Sarajevu.", 43.8665427, 18.3195949),
            LocationSeed("osm-loc-nedzarici", "Nedžarići", "Naselje u Sarajevu.", 43.8367867, 18.3364819),
            LocationSeed("osm-loc-osjek", "Osjek", "Naselje u Sarajevu.", 43.8435516, 18.277292),
            LocationSeed("osm-loc-otes", "Otes", "Naselje u Sarajevu.", 43.8408185, 18.2969773),
            LocationSeed("osm-loc-otoka", "Otoka", "Naselje u Sarajevu.", 43.8474072, 18.3655732),
            LocationSeed("osm-loc-pavlovac", "Pavlovac", "Naselje u Sarajevu.", 43.811998, 18.3753523),
            LocationSeed("osm-loc-pejton", "Pejton", "Naselje u Sarajevu.", 43.8353483, 18.302551),
            LocationSeed("osm-loc-podhrastovi", "Podhrastovi", "Naselje u Sarajevu.", 43.8685862, 18.4235918),
            LocationSeed("osm-loc-pofalici-i", "Pofalići I", "Naselje u Sarajevu.", 43.8622786, 18.3875445),
            LocationSeed("osm-loc-pofalici-ii", "Pofalići II", "Naselje u Sarajevu.", 43.8603727, 18.3772064),
            LocationSeed("osm-loc-radica-potok", "Radića potok", "Naselje u Sarajevu.", 43.9091269, 18.3475878),
            LocationSeed("osm-loc-rajlovac", "Rajlovac", "Naselje u Sarajevu.", 43.8786557, 18.3121057),
            LocationSeed("osm-loc-reljevo", "Reljevo", "Naselje u Sarajevu.", 43.8878098, 18.3119075),
            LocationSeed("osm-loc-rosulje", "Rosulje", "Naselje u Sarajevu.", 43.9033656, 18.3522963),
            LocationSeed("osm-loc-sedrenik", "Sedrenik", "Naselje u Sarajevu.", 43.8729546, 18.434655),
            LocationSeed("osm-loc-skenderija", "Skenderija", "Naselje u Sarajevu.", 43.8551592, 18.4139241),
            LocationSeed("osm-loc-socijalno", "Socijalno", "Naselje u Sarajevu.", 43.8530077, 18.3853281),
            LocationSeed("osm-loc-soko", "Soko", "Naselje u Sarajevu.", 43.8243381, 18.3569368),
            LocationSeed("osm-loc-sokolovic-kolonija", "Sokolović-kolonija", "Naselje u Sarajevu.", 43.8136895, 18.3155943),
            LocationSeed("osm-loc-soukbunar", "Soukbunar", "Naselje u Sarajevu.", 43.8503081, 18.4145842),
            LocationSeed("osm-loc-stup", "Stup", "Naselje u Sarajevu.", 43.8428262, 18.3243),
            LocationSeed("osm-loc-stupsko-brdo", "Stupsko Brdo", "Naselje u Sarajevu.", 43.8402336, 18.3337406),
            LocationSeed("osm-loc-sumbulusa", "Sumbuluša", "Naselje u Sarajevu.", 43.8636576, 18.4360116),
            LocationSeed("osm-loc-turkusici", "Turkušići", "Naselje u Sarajevu.", 43.8347519, 18.3598373),
            LocationSeed("osm-loc-velesici", "Velešići", "Naselje u Sarajevu.", 43.865026, 18.3956098),
            LocationSeed("osm-loc-veljine", "Veljine", "Naselje u Sarajevu.", 43.8253416, 18.3597714),
            LocationSeed("osm-loc-vitkovac", "Vitkovac", "Naselje u Sarajevu.", 43.8596393, 18.3396992),
            LocationSeed("osm-loc-vojnicko-polje", "Vojničko polje", "Naselje u Sarajevu.", 43.8411129, 18.340426),
            LocationSeed("osm-loc-vraca", "Vraca", "Naselje u Sarajevu.", 43.8453046, 18.3948453),
            LocationSeed("osm-loc-vranjes", "Vranješ", "Naselje u Sarajevu.", 43.817619, 18.3723485),
            LocationSeed("osm-loc-vratnik", "Vratnik", "Naselje u Sarajevu.", 43.8623357, 18.4406951),
            LocationSeed("osm-loc-vreoca", "Vreoca", "Naselje u Sarajevu.", 43.8343861, 18.2894414),
            LocationSeed("osm-loc-zabrde", "Zabrđe", "Naselje u Sarajevu.", 43.8763839, 18.3212035),
            LocationSeed("osm-loc-cengic-vila-i", "Čengić Vila I", "Naselje u Sarajevu.", 43.8508596, 18.3694409),
            LocationSeed("osm-loc-cengic-vila-ii", "Čengić Vila II", "Naselje u Sarajevu.", 43.8477057, 18.3707873),
            LocationSeed("osm-loc-sip", "Šip", "Naselje u Sarajevu.", 43.88139, 18.3968664),
            LocationSeed("osm-loc-sirokaca", "Širokača", "Naselje u Sarajevu.", 43.8516902, 18.4250643),
            LocationSeed("osm-loc-svrakino-selo", "Švrakino selo", "Naselje u Sarajevu.", 43.8422588, 18.3579112),
        )

        osmLocations.forEach { location ->
            val exists = Locations
                .select(Locations.id)
                .where { Locations.id eq location.id }
                .count() > 0

            if (!exists) {
                Locations.insert {
                    it[Locations.id] = location.id
                    it[Locations.name] = location.name
                    it[Locations.description] = location.description
                    it[Locations.latitude] = location.latitude
                    it[Locations.longitude] = location.longitude
                }
            }
        }

        println("Seeded Sarajevo locations.")
    }

    private fun seedMusics() {
        val musics = listOf(
            MusicSeed("seed-music-01", "Lovely Day", "Bill Withers", 244),
            MusicSeed("seed-music-02", "Sunday Morning", "Maroon 5", 241),
            MusicSeed("seed-music-03", "Put Your Records On", "Corinne Bailey Rae", 210),
            MusicSeed("seed-music-04", "Here Comes the Sun", "The Beatles", 185),
            MusicSeed("seed-music-05", "Golden", "Harry Styles", 208),
            MusicSeed("seed-music-06", "Walking on Sunshine", "Katrina & The Waves", 239),
            MusicSeed("seed-music-07", "Good Days", "SZA", 279),
            MusicSeed("seed-music-08", "Electric Feel", "MGMT", 229),
            MusicSeed("seed-music-09", "Sweater Weather", "The Neighbourhood", 240),
            MusicSeed("seed-music-10", "The Night We Met", "Lord Huron", 208),
            MusicSeed("seed-music-11", "Apocalypse", "Cigarettes After Sex", 269),
            MusicSeed("seed-music-12", "Riptide", "Vance Joy", 204),
            MusicSeed("seed-music-13", "Sunset Lover", "Petit Biscuit", 237),
            MusicSeed("seed-music-14", "Pink + White", "Frank Ocean", 184),
            MusicSeed("seed-music-15", "Die With A Smile", "Lady Gaga & Bruno Mars", 251),
            MusicSeed("seed-music-16", "Blinding Lights", "The Weeknd", 200),
            MusicSeed("seed-music-17", "Levitating", "Dua Lipa", 203),
            MusicSeed("seed-music-18", "As It Was", "Harry Styles", 167),
            MusicSeed("seed-music-19", "Watermelon Sugar", "Harry Styles", 174),
            MusicSeed("seed-music-20", "Heat Waves", "Glass Animals", 238),
            MusicSeed("seed-music-21", "Can't Help Falling in Love", "Elvis Presley", 182),
            MusicSeed("seed-music-22", "Until I Found You", "Stephen Sanchez", 177),
            MusicSeed("seed-music-23", "Yellow", "Coldplay", 266),
            MusicSeed("seed-music-24", "Midnight City", "M83", 243),
            MusicSeed("seed-music-25", "I Wanna Dance with Somebody", "Whitney Houston", 293)
        )

        musics.forEach { music ->
            val exists = Musics
                .select(Musics.id)
                .where { Musics.id eq music.id }
                .count() > 0

            if (!exists) {
                Musics.insert {
                    it[Musics.id] = music.id
                    it[Musics.title] = music.title
                    it[Musics.artist] = music.artist
                    it[Musics.duration] = music.duration
                    it[Musics.albumImageUrl] = null
                }
            }
        }

        println("Seeded music.")
    }

    private fun seedPlaylists() {
        val playlists = listOf(
            PlaylistSeed("seed-playlist-morning", "Sarajevo Morning", "Indie Pop", "Chill", "Clear", "15-22°C", "Sarajevo", "Alone,Coffee,Work", 18, "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"),
            PlaylistSeed("seed-playlist-sunny", "Sunny Sarajevo", "Pop", "Feel Good", "Sunny", "20-30°C", "Sarajevo", "Friends,Outdoor,Day Out", 34, "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"),
            PlaylistSeed("seed-playlist-rainy", "Rainy Baščaršija", "Indie", "Cozy", "Rain", "8-18°C", "Baščaršija", "Alone,Couple,Coffee", 27, "https://www.youtube.com/watch?v=j7X3vq6GY2c"),
            PlaylistSeed("seed-playlist-sunset", "Sarajevo Sunset", "R&B", "Romantic", "Cloudy", "12-24°C", "Trebević", "Couple,Date,Relax", 41, "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"),
            PlaylistSeed("seed-playlist-night", "Sarajevo Night", "Pop", "Energetic", "Clear", "10-25°C", "Marijin Dvor", "Friends,Night Out,Party", 56, "https://www.youtube.com/playlist?list=PLkShY3_KwgIIHz8OsRyAu2dTQcHg1dmWl"),
            PlaylistSeed("seed-playlist-rock", "Sarajevo Rock", "Rock", "Energetic", "Cloudy", "10-22°C", "Sarajevo", "Friends,Night Out", 44, "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"),
            PlaylistSeed("seed-playlist-hiphop", "Baščaršija Beats", "Hip-Hop", "Energetic", "Clear", "15-28°C", "Baščaršija", "Friends,Work,Focus", 38, "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"),
            PlaylistSeed("seed-playlist-sevdah", "Sevdah Nights", "Sevdah", "Romantic", "Clear", "12-22°C", "Baščaršija", "Couple,Relax,Coffee", 52, "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"),
            PlaylistSeed("seed-playlist-electronic", "Sarajevo Electronic", "Electronic", "Energetic", "Clear", "12-26°C", "Marijin Dvor", "Friends,Night Out,Party", 48, "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"),
            PlaylistSeed("seed-playlist-jazz", "Late Night Jazz", "Jazz", "Chill", "Rain", "10-20°C", "Bistrik", "Alone,Couple,Relax", 36, "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"),
            PlaylistSeed("seed-playlist-classical", "Sarajevo Classical", "Classical", "Focused", "Clear", "12-24°C", "Vijećnica", "Alone,Focus,Work", 29, "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"),
            PlaylistSeed("seed-playlist-indie", "Indie Sarajevo", "Indie", "Chill", "Cloudy", "10-22°C", "Skenderija", "Alone,Couple,Coffee", 33, "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR")
        )

        playlists.forEach { playlist ->
            val exists = Playlists
                .select(Playlists.id)
                .where { Playlists.id eq playlist.id }
                .count() > 0

            if (!exists) {
                Playlists.insert {
                    it[Playlists.id] = playlist.id
                    it[Playlists.title] = playlist.title
                    it[Playlists.genre] = playlist.genre
                    it[Playlists.mood] = playlist.mood
                    it[Playlists.albumImageUrl] = null
                    it[Playlists.weather] = playlist.weather
                    it[Playlists.temperature] = playlist.temperature
                    it[Playlists.location] = playlist.location
                    it[Playlists.likes] = playlist.likes
                    it[Playlists.spotifyUrl] = null
                    it[Playlists.youtubeUrl] = playlist.youtubeUrl
                    it[Playlists.bestFor] = playlist.bestFor
                }
            }
        }

        println("Seeded playlists.")
    }

    private fun seedPlaylistSongs() {
        val playlistSongs = mapOf(
            "seed-playlist-morning" to listOf("seed-music-01", "seed-music-02", "seed-music-03", "seed-music-04", "seed-music-05"),
            "seed-playlist-sunny" to listOf("seed-music-05", "seed-music-06", "seed-music-08", "seed-music-17", "seed-music-19"),
            "seed-playlist-rainy" to listOf("seed-music-09", "seed-music-10", "seed-music-11", "seed-music-12", "seed-music-20"),
            "seed-playlist-sunset" to listOf("seed-music-07", "seed-music-13", "seed-music-14", "seed-music-15", "seed-music-22"),
            "seed-playlist-night" to listOf("seed-music-16", "seed-music-17", "seed-music-18", "seed-music-24", "seed-music-25"),
            "seed-playlist-rock" to listOf("seed-music-08", "seed-music-16", "seed-music-23", "seed-music-24", "seed-music-20"),
            "seed-playlist-hiphop" to listOf("seed-music-07", "seed-music-16", "seed-music-17", "seed-music-24", "seed-music-25"),
            "seed-playlist-sevdah" to listOf("seed-music-21", "seed-music-22", "seed-music-13", "seed-music-07", "seed-music-14"),
            "seed-playlist-electronic" to listOf("seed-music-13", "seed-music-16", "seed-music-24", "seed-music-08", "seed-music-20"),
            "seed-playlist-jazz" to listOf("seed-music-21", "seed-music-03", "seed-music-14", "seed-music-10", "seed-music-15"),
            "seed-playlist-classical" to listOf("seed-music-21", "seed-music-22", "seed-music-15", "seed-music-03", "seed-music-13"),
            "seed-playlist-indie" to listOf("seed-music-09", "seed-music-10", "seed-music-11", "seed-music-12", "seed-music-23")
        )

        playlistSongs.forEach { (playlistId, musicIds) ->
            musicIds.forEach { musicId ->
                val exists = PlaylistSongs
                    .select(PlaylistSongs.playlistId)
                    .where {
                        (PlaylistSongs.playlistId eq playlistId) and
                                (PlaylistSongs.musicId eq musicId)
                    }
                    .count() > 0

                if (!exists) {
                    PlaylistSongs.insert {
                        it[PlaylistSongs.playlistId] = playlistId
                        it[PlaylistSongs.musicId] = musicId
                    }
                }
            }
        }

        println("Seeded playlist songs.")
    }

    private fun seedOutfits() {
        val outfits = listOf(
            OutfitSeed("seed-outfit-clear-01", SEED_USER_01, "Sunny City Casual", "Clear", "Summer", "Zara", "Sarajevo City Center, Vrbanja 1", "60-100 KM", 24, "https://picsum.photos/seed/outfit-clear-01/400/600", "080 083 080", "https://www.zara.com/ba/"),
            OutfitSeed("seed-outfit-clear-02", SEED_USER_02, "Bright Day Layers", "Clear", "Spring", "Mango", "Sarajevo City Center, Vrbanja 1", "80-140 KM", 32, "https://picsum.photos/seed/outfit-clear-02/400/600", "38733271360", "https://shop.mango.com/ba"),
            OutfitSeed("seed-outfit-clear-03", SEED_USER_03, "Weekend in Baščaršija", "Clear", "Summer", "H&M", "Sarajevo City Center, Vrbanja 1", "50-120 KM", 18, "https://picsum.photos/seed/outfit-clear-03/400/600", null, "https://www2.hm.com/ba_bs/index.html"),
            OutfitSeed("seed-outfit-clear-04", SEED_USER_04, "Golden Hour Walk", "Clear", "Autumn", "Pull&Bear", "Sarajevo City Center, Vrbanja 1", "60-110 KM", 27, "https://picsum.photos/seed/outfit-clear-04/400/600", null, "https://www.pullandbear.com/ba/"),
            OutfitSeed("seed-outfit-clear-05", SEED_USER_05, "City Chic", "Clear", "Summer", "Bershka", "Sarajevo City Center, Vrbanja 1", "70-130 KM", 35, "https://picsum.photos/seed/outfit-clear-05/400/600", "033 957-772", "https://www.bershka.com/ba/"),
            OutfitSeed("seed-outfit-clouds-01", SEED_USER_01, "Coffee & Chill", "Clouds", "Spring", "LC Waikiki", "Bingo City Center, Ilidža", "40-80 KM", 18, "https://picsum.photos/seed/outfit-clouds-01/400/600", "+387 33 873 368", "https://www.lcwaikiki.com/ba/"),
            OutfitSeed("seed-outfit-clouds-02", SEED_USER_02, "Overcast Neutrals", "Clouds", "Autumn", "Reserved", "Sarajevo City Center, Vrbanja 1", "80-150 KM", 41, "https://picsum.photos/seed/outfit-clouds-02/400/600", null, "https://www.reserved.com/ba/"),
            OutfitSeed("seed-outfit-clouds-03", SEED_USER_03, "Layered Look", "Clouds", "Spring", "Zara", "Sarajevo City Center, Vrbanja 1", "70-140 KM", 22, "https://picsum.photos/seed/outfit-clouds-03/400/600", "080 083 080", "https://www.zara.com/ba/"),
            OutfitSeed("seed-outfit-clouds-04", SEED_USER_04, "Muted Tones", "Clouds", "Autumn", "Mango", "Sarajevo City Center, Vrbanja 1", "90-160 KM", 29, "https://picsum.photos/seed/outfit-clouds-04/400/600", "38733271360", "https://shop.mango.com/ba"),
            OutfitSeed("seed-outfit-clouds-05", SEED_USER_05, "Grey Day Comfort", "Clouds", "Winter", "H&M", "Sarajevo City Center, Vrbanja 1", "50-110 KM", 16, "https://picsum.photos/seed/outfit-clouds-05/400/600", null, "https://www2.hm.com/ba_bs/index.html"),
            OutfitSeed("seed-outfit-rain-01", SEED_USER_01, "Rainy Day Layers", "Rain", "Autumn", "Reserved", "Sarajevo City Center, Vrbanja 1", "80-140 KM", 31, "https://picsum.photos/seed/outfit-rain-01/400/600", null, "https://www.reserved.com/ba/"),
            OutfitSeed("seed-outfit-rain-02", SEED_USER_02, "Waterproof Style", "Rain", "Spring", "Sport Vision", "Džidžikovac, Sarajevo", "120-220 KM", 27, "https://picsum.photos/seed/outfit-rain-02/400/600", "(+387) 33 733 045", "https://www.sportvision.ba/"),
            OutfitSeed("seed-outfit-rain-03", SEED_USER_03, "City Raincoat", "Rain", "Autumn", "Zara", "Sarajevo City Center, Vrbanja 1", "100-180 KM", 38, "https://picsum.photos/seed/outfit-rain-03/400/600", "080 083 080", "https://www.zara.com/ba/"),
            OutfitSeed("seed-outfit-rain-04", SEED_USER_04, "Rainy Commute", "Rain", "Winter", "LC Waikiki", "Bingo City Center, Ilidža", "60-120 KM", 21, "https://picsum.photos/seed/outfit-rain-04/400/600", "+387 33 873 368", "https://www.lcwaikiki.com/ba/"),
            OutfitSeed("seed-outfit-rain-05", SEED_USER_05, "Storm Ready", "Rain", "Autumn", "Sport Reality", "Stup, Sarajevo", "130-240 KM", 44, "https://picsum.photos/seed/outfit-rain-05/400/600", null, "https://sportreality.ba/"),
            OutfitSeed("seed-outfit-drizzle-01", SEED_USER_01, "Light Mist Layers", "Drizzle", "Spring", "H&M", "Sarajevo City Center, Vrbanja 1", "50-100 KM", 19, "https://picsum.photos/seed/outfit-drizzle-01/400/600", null, "https://www2.hm.com/ba_bs/index.html"),
            OutfitSeed("seed-outfit-drizzle-02", SEED_USER_02, "Drizzly Day Out", "Drizzle", "Autumn", "Mango", "Sarajevo City Center, Vrbanja 1", "80-150 KM", 26, "https://picsum.photos/seed/outfit-drizzle-02/400/600", "38733271360", "https://shop.mango.com/ba"),
            OutfitSeed("seed-outfit-drizzle-03", SEED_USER_03, "Soft Rain Style", "Drizzle", "Spring", "Zara", "Sarajevo City Center, Vrbanja 1", "90-160 KM", 33, "https://picsum.photos/seed/outfit-drizzle-03/400/600", "080 083 080", "https://www.zara.com/ba/"),
            OutfitSeed("seed-outfit-drizzle-04", SEED_USER_04, "Urban Drizzle", "Drizzle", "Autumn", "Pull&Bear", "Sarajevo City Center, Vrbanja 1", "60-110 KM", 24, "https://picsum.photos/seed/outfit-drizzle-04/400/600", null, "https://www.pullandbear.com/ba/"),
            OutfitSeed("seed-outfit-drizzle-05", SEED_USER_05, "Cozy Overcast", "Drizzle", "Winter", "Reserved", "Sarajevo City Center, Vrbanja 1", "100-180 KM", 39, "https://picsum.photos/seed/outfit-drizzle-05/400/600", null, "https://www.reserved.com/ba/"),
            OutfitSeed("seed-outfit-snow-01", SEED_USER_01, "Winter Walk", "Snow", "Winter", "Sport Reality", "Stup, Sarajevo", "150-280 KM", 35, "https://picsum.photos/seed/outfit-snow-01/400/600", null, "https://sportreality.ba/"),
            OutfitSeed("seed-outfit-snow-02", SEED_USER_02, "Snow Day Layers", "Snow", "Winter", "Zara", "Sarajevo City Center, Vrbanja 1", "120-220 KM", 42, "https://picsum.photos/seed/outfit-snow-02/400/600", "080 083 080", "https://www.zara.com/ba/"),
            OutfitSeed("seed-outfit-snow-03", SEED_USER_03, "Warm Winter Coat", "Snow", "Winter", "Reserved", "Sarajevo City Center, Vrbanja 1", "140-260 KM", 48, "https://picsum.photos/seed/outfit-snow-03/400/600", null, "https://www.reserved.com/ba/"),
            OutfitSeed("seed-outfit-snow-04", SEED_USER_04, "Alpine Casual", "Snow", "Winter", "Sport Vision", "Džidžikovac, Sarajevo", "160-300 KM", 51, "https://picsum.photos/seed/outfit-snow-04/400/600", "(+387) 33 733 045", "https://www.sportvision.ba/"),
            OutfitSeed("seed-outfit-snow-05", SEED_USER_05, "Snowy Street Style", "Snow", "Winter", "Mango", "Sarajevo City Center, Vrbanja 1", "130-240 KM", 37, "https://picsum.photos/seed/outfit-snow-05/400/600", "38733271360", "https://shop.mango.com/ba"),
            OutfitSeed("seed-outfit-thunder-01", SEED_USER_01, "Storm Proof", "Thunderstorm", "Autumn", "Sport Reality", "Stup, Sarajevo", "150-270 KM", 28, "https://picsum.photos/seed/outfit-thunder-01/400/600", null, "https://sportreality.ba/"),
            OutfitSeed("seed-outfit-thunder-02", SEED_USER_02, "Thunder Ready", "Thunderstorm", "Summer", "Sport Vision", "Džidžikovac, Sarajevo", "140-260 KM", 22, "https://picsum.photos/seed/outfit-thunder-02/400/600", "(+387) 33 733 045", "https://www.sportvision.ba/"),
            OutfitSeed("seed-outfit-thunder-03", SEED_USER_03, "Rain Shield Style", "Thunderstorm", "Spring", "Zara", "Sarajevo City Center, Vrbanja 1", "100-190 KM", 30, "https://picsum.photos/seed/outfit-thunder-03/400/600", "080 083 080", "https://www.zara.com/ba/"),
            OutfitSeed("seed-outfit-thunder-04", SEED_USER_04, "Heavy Weather Layers", "Thunderstorm", "Autumn", "H&M", "Sarajevo City Center, Vrbanja 1", "80-150 KM", 25, "https://picsum.photos/seed/outfit-thunder-04/400/600", null, "https://www2.hm.com/ba_bs/index.html"),
            OutfitSeed("seed-outfit-thunder-05", SEED_USER_05, "Stormy City", "Thunderstorm", "Summer", "LC Waikiki", "Bingo City Center, Ilidža", "70-140 KM", 20, "https://picsum.photos/seed/outfit-thunder-05/400/600", "+387 33 873 368", "https://www.lcwaikiki.com/ba/")
        )

        outfits.forEach { outfit ->
            val exists = Outfits
                .select(Outfits.id)
                .where { Outfits.id eq outfit.id }
                .count() > 0

            if (!exists) {
                Outfits.insert {
                    it[Outfits.id] = outfit.id
                    it[Outfits.userId] = outfit.userId
                    it[Outfits.imageUrl] = outfit.imageUrl
                    it[Outfits.title] = outfit.title
                    it[Outfits.weatherCondition] = outfit.weatherCondition
                    it[Outfits.season] = outfit.season
                    it[Outfits.likes] = outfit.likes
                    it[Outfits.createdAt] = Instant.now()
                    it[Outfits.storeName] = outfit.storeName
                    it[Outfits.storeAddress] = outfit.storeAddress
                    it[Outfits.price] = outfit.price
                    it[Outfits.storePhone] = outfit.storePhone
                    it[Outfits.productUrl] = outfit.productUrl
                }
            }
        }

        println("Seeded outfits.")
    }

    private fun seedPlaces() {
        val places = listOf(
            PlaceSeed("seed-place-01", "Ćevabdžinica Željo", "Restaurant", "seed-location-bascarsija", "Kundurdžiluk, Sarajevo", 43.8599, 18.4298, "Traditional Sarajevo ćevapi and a classic Baščaršija atmosphere.", 4.6),
            PlaceSeed("seed-place-02", "Morica Han", "Cafe", "seed-location-bascarsija", "Sarači, Sarajevo", 43.8610, 18.4301, "Historic courtyard and traditional coffee atmosphere.", 4.5),
            PlaceSeed("seed-place-03", "Vijećnica", "Attraction", "seed-location-vijecnica", "Obala Kulina Bana, Sarajevo", 43.8595, 18.4331, "Historic Sarajevo landmark beside the Miljacka.", 4.8),
            PlaceSeed("seed-place-04", "Yellow Fortress Viewpoint", "Viewpoint", "seed-location-bijela-tabija", "Vratnik, Sarajevo", 43.8624, 18.4437, "Panoramic viewpoint overlooking Sarajevo.", 4.8),
            PlaceSeed("seed-place-05", "Vrelo Bosne Park", "Park", "seed-location-vrelo-bosne", "Ilidža, Sarajevo", 43.8196, 18.2695, "Natural park ideal for walking and family trips.", 4.7),
            PlaceSeed("seed-place-06", "Trebević Viewpoint", "Outdoor", "seed-location-trebevic", "Trebević, Sarajevo", 43.8271, 18.4485, "Mountain viewpoint with panoramic city views.", 4.8),
            PlaceSeed("seed-place-07", "Avaz Sky View", "Viewpoint", "seed-location-avaz", "Marijin Dvor, Sarajevo", 43.8584, 18.4055, "High-altitude city viewpoint.", 4.4),
            PlaceSeed("seed-place-08", "Marijin Dvor Coffee", "Cafe", "seed-location-marijin-dvor", "Marijin Dvor, Sarajevo", 43.8564, 18.4105, "Urban coffee area close to the city centre.", 4.3),
            PlaceSeed("seed-place-09", "Skenderija Walk", "Outdoor", "seed-location-skenderija", "Skenderija, Sarajevo", 43.8535, 18.4078, "Riverside area for a relaxed afternoon walk.", 4.2),
            PlaceSeed("seed-place-10", "Koševo Green Area", "Park", "seed-location-kosevo", "Koševo, Sarajevo", 43.8702, 18.4075, "Green urban area for walking and relaxing.", 4.3),
            PlaceSeed("seed-place-11", "Ciglane View", "Viewpoint", "seed-location-ciglane", "Ciglane, Sarajevo", 43.8654, 18.4070, "Urban viewpoint over central Sarajevo.", 4.1),
            PlaceSeed("seed-place-12", "Pofalići Cafe District", "Cafe", "seed-location-pofalici", "Pofalići, Sarajevo", 43.8546, 18.3898, "Neighbourhood area with cafes and restaurants.", 4.2),
            PlaceSeed("seed-place-13", "Otoka Recreation Area", "Outdoor", "seed-location-otoka", "Otoka, Sarajevo", 43.8465, 18.3667, "Casual recreation area for meeting friends.", 4.2),
            PlaceSeed("seed-place-14", "Čengić Vila Walk", "Outdoor", "seed-location-cengic-vila", "Čengić Vila, Sarajevo", 43.8447, 18.3650, "Urban walking area connected with the tram network.", 4.1),
            PlaceSeed("seed-place-15", "Stup Shopping Area", "Shopping", "seed-location-stup", "Stup, Sarajevo", 43.8397, 18.3218, "Shopping and dining area.", 4.0),
            PlaceSeed("seed-place-16", "Bistrik Old Town Walk", "Outdoor", "seed-location-bistrik", "Bistrik, Sarajevo", 43.8547, 18.4298, "Historic neighbourhood for walking and exploring.", 4.5),
            PlaceSeed("seed-place-17", "Vraca Viewpoint", "Viewpoint", "seed-location-vraca", "Vraca, Sarajevo", 43.8448, 18.3935, "Quiet viewpoint over southern Sarajevo.", 4.4),
            PlaceSeed("seed-place-18", "Sebilj Square", "Attraction", "seed-location-sebilj", "Baščaršija, Sarajevo", 43.8607, 18.4290, "Central meeting point in the heart of Baščaršija.", 4.8),
            PlaceSeed("seed-place-19", "Zmajevac Sunset Spot", "Viewpoint", "seed-location-zmajevac", "Zmajevac, Sarajevo", 43.8672, 18.4122, "Popular place for coffee and sunset views.", 4.6),
            PlaceSeed("seed-place-20", "Katedrala Square", "Attraction", "seed-location-katedrala", "Ferhadija, Sarajevo", 43.8593, 18.4248, "Historic central square surrounded by Sarajevo landmarks.", 4.6)
        )

        places.forEach { place ->
            val exists = Places
                .select(Places.id)
                .where { Places.id eq place.id }
                .count() > 0

            if (!exists) {
                val venue = Locations
                    .select(Locations.id)
                    .where { Locations.id eq place.venueName }
                    .singleOrNull()

                Places.insert {
                    it[Places.id] = place.id
                    it[Places.name] = place.name
                    it[Places.category] = place.category
                    it[Places.venueId] = venue?.get(Locations.id)
                    it[Places.address] = place.address
                    it[Places.latitude] = place.latitude
                    it[Places.longitude] = place.longitude
                    it[Places.description] = place.description
                    it[Places.imageUrl] = null
                    it[Places.rating] = place.rating
                    it[Places.createdAt] = Instant.now()
                }
            }
        }

        val osmCafes = listOf(
            PlaceSeed("osm-cafe-492137265", "Art", "Cafe", "", "", 43.867595, 18.4122772, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-938872352", "Coccinelle", "Cafe", "", "", 43.8585111, 18.4024088, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1234334175", "Coffee Station", "Cafe", "", "", 43.8287705, 18.3458721, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1234438782", "Caffé slastičarna Kup", "Cafe", "", "", 43.8295798, 18.3530992, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1305811322", "Kafanica", "Cafe", "", "", 43.8689867, 18.4059183, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1307576634", "Dezert", "Cafe", "", "", 43.8286667, 18.340349, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1313668803", "SO.BA", "Cafe", "", "", 43.8679342, 18.4129938, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1314030921", "Caffe London", "Cafe", "", "", 43.8652336, 18.4093962, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1314030923", "Caffe Paris", "Cafe", "", "", 43.8649969, 18.4093361, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1374933816", "Stella", "Cafe", "", "", 43.8549379, 18.3925676, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1415371399", "Vitraz", "Cafe", "", "", 43.9009838, 18.3447189, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1415371403", "Azra", "Cafe", "", "", 43.901009, 18.3445932, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1427483920", "Caffe Park", "Cafe", "", "", 43.901708, 18.3420123, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1502091202", "Luxor", "Cafe", "", "", 43.8196677, 18.3641758, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1502091205", "Sydney II", "Cafe", "", "", 43.8195076, 18.3622692, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1502091208", "Ogi", "Cafe", "", "", 43.8227774, 18.3552108, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1502091209", "Resume", "Cafe", "", "", 43.8205834, 18.3632219, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1502091210", "Piano", "Cafe", "", "", 43.8192367, 18.3638671, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1502708000", "Kod Spomenka", "Cafe", "", "", 43.9077466, 18.4603553, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1506250053", "Palma", "Cafe", "", "", 43.8490286, 18.3856871, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1506274157", "Valentino", "Cafe", "", "", 43.850166, 18.3852901, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1559277423", "Segafredo", "Cafe", "", "", 43.849422, 18.3916407, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1676577470", "Caffe bar Fancy", "Cafe", "", "", 43.839931, 18.3413918, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1676577836", "Caffe bar New York", "Cafe", "", "", 43.841434, 18.3406121, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1684372379", "Cafe-bar Đea", "Cafe", "", "", 43.8431653, 18.3525832, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1684383458", "Cafe-bar GOLD", "Cafe", "", "", 43.8421013, 18.3426849, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1684384766", "Caffe slastičarna - pizzeria Fellini", "Cafe", "", "", 43.8445659, 18.342262, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1740557553", "Caffe Club Le Figaro", "Cafe", "", "", 43.8523361, 18.3779925, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1758524638", "Cappuccino", "Cafe", "", "", 43.819227, 18.3631038, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1821606547", "Verona", "Cafe", "", "", 43.821587, 18.3651456, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1889511881", "Gradski Bar", "Cafe", "", "", 43.8230216, 18.2056728, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1904838174", "Kafe galerija", "Cafe", "", "", 43.8206808, 18.3590416, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1918833495", "CAFFE BAR V.I.P.", "Cafe", "", "", 43.9609183, 18.2671939, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1920598265", "Orange", "Cafe", "", "", 43.822522, 18.3572502, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1922342697", "Roma", "Cafe", "", "", 43.8267796, 18.3659199, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1928842503", "Meeting Point", "Cafe", "", "", 43.8556219, 18.4182292, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-1933113958", "Caffe \"Mistik\"", "Cafe", "", "", 43.9611732, 18.2672342, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2003829841", "Spazio", "Cafe", "", "", 43.8574792, 18.4172202, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2005544547", "Papagaj", "Cafe", "", "", 43.8522194, 18.3956633, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2005572843", "Aquarius", "Cafe", "", "", 43.849937, 18.3920186, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2006881409", "Vatra", "Cafe", "", "", 43.852834, 18.40116, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2006881497", "CHE2", "Cafe", "", "", 43.8519935, 18.4028619, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2006951052", "Vrt", "Cafe", "", "", 43.853186, 18.4005736, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2007221618", "VIP caffe club", "Cafe", "", "", 43.8561074, 18.4195944, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2007221928", "Linea M", "Cafe", "", "", 43.8561036, 18.4194952, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2007962354", "Mash", "Cafe", "", "", 43.8575073, 18.4215661, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2007962407", "Kogo", "Cafe", "", "", 43.8591202, 18.4205227, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2007962650", "Michele's", "Cafe", "", "", 43.8588785, 18.4246828, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2007962718", "Impasto", "Cafe", "", "", 43.8588978, 18.4247391, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2007962982", "Vatra", "Cafe", "", "", 43.8585981, 18.4222661, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2007963209", "Brasil", "Cafe", "", "", 43.8594335, 18.420528, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2007963244", "Delikatesna Radnia", "Cafe", "", "", 43.8567675, 18.4219845, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2010483918", "Komshilook", "Cafe", "", "", 43.830286, 18.339529, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2010490132", "FACES", "Cafe", "", "", 43.866101, 18.408481, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2012076461", "A2", "Cafe", "", "", 43.8507443, 18.3918519, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2102967519", "Soho", "Cafe", "", "", 43.8269287, 18.3691617, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2107013473", "Dialog", "Cafe", "", "", 43.8584964, 18.4235381, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2107067570", "Cafe Hotel Hecco", "Cafe", "", "", 43.8586477, 18.4217497, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2107075697", "Julius Meinl", "Cafe", "", "", 43.85792, 18.4155478, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2122714181", "Mirage", "Cafe", "", "", 43.8241414, 18.3623226, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2132236803", "El Kazbah", "Cafe", "", "", 43.8594291, 18.4304459, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2134792480", "Curry", "Cafe", "", "", 43.8523329, 18.4031685, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2136025433", "Muzički paviljon - At mejdan", "Cafe", "", "", 43.8570547, 18.4282809, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2145389745", "Caffe Viktorija", "Cafe", "", "", 43.8218102, 18.2034844, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2163545844", "Ljetna bašta Stojčevac", "Cafe", "", "", 43.8098117, 18.2931404, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2169681620", "Moskva", "Cafe", "", "", 43.8217652, 18.3642362, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2276889686", "Galerija Boris Smoje", "Cafe", "", "", 43.8573282, 18.417248, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2311542032", "Parkuša", "Cafe", "", "", 43.8591016, 18.417452, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2322179281", "Divne Stvari", "Cafe", "", "", 43.8565409, 18.4147541, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2322230236", "Boulevard", "Cafe", "", "", 43.857038, 18.4059925, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2322234124", "Academia del caffe", "Cafe", "", "", 43.8484151, 18.3795047, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2322257668", "Azzuro", "Cafe", "", "", 43.8486892, 18.3733748, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2322291641", "Film", "Cafe", "", "", 43.8314651, 18.3052085, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2322306311", "Srce", "Cafe", "", "", 43.828853, 18.3024217, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2379851032", "Kafana", "Cafe", "", "", 43.8241076, 18.3550598, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2380370962", "Kafana", "Cafe", "", "", 43.9026491, 18.3433166, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2381601799", "Maranello", "Cafe", "", "", 43.901955, 18.3403969, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2393814663", "Gala", "Cafe", "", "", 43.8511465, 18.372544, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2421002178", "Coffee Time", "Cafe", "", "", 43.8588606, 18.3967284, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2496733533", "Bosanska Kafa", "Cafe", "", "", 43.8596678, 18.4319118, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2523293560", "Coffee \"HOUSE\"", "Cafe", "", "", 43.85554, 18.4174831, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2529430092", "Caffe Enzzo", "Cafe", "", "", 43.8303892, 18.3086811, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2529430185", "Caffe Club Trendy", "Cafe", "", "", 43.8303783, 18.3084808, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2529433036", "Music Club PREMIUM", "Cafe", "", "", 43.829903, 18.309134, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2888213025", "B-faza", "Cafe", "", "", 43.8417267, 18.3446848, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2893227109", "Fratelo", "Cafe", "", "", 43.8467065, 18.3548092, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2895940266", "BENJO", "Cafe", "", "", 43.8399813, 18.3438972, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2895940268", "ASPEK", "Cafe", "", "", 43.8426951, 18.3517669, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2905508694", "Playkids", "Cafe", "", "", 43.8457387, 18.3499597, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-2922315023", "Brazil", "Cafe", "", "", 43.8510351, 18.3957501, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3015930359", "Coppa d'Oro", "Cafe", "", "", 43.8447258, 18.3431155, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3104655030", "slastičarna Ideja", "Cafe", "", "", 43.87524, 18.4122889, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3296379461", "Venecia", "Cafe", "", "", 43.8502317, 18.3960751, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3320385090", "My name is Luka", "Cafe", "", "", 43.8289639, 18.371883, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3359821480", "Svijet Čokolade", "Cafe", "", "", 43.8557467, 18.4201814, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3367674059", "Sport", "Cafe", "", "", 43.8271189, 18.358579, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3377229210", "JAZZ Radio Caffe", "Cafe", "", "", 43.8490801, 18.3819829, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3514061076", "La Verna", "Cafe", "", "", 43.8172455, 18.3603241, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3631168631", "Square", "Cafe", "", "", 43.8290939, 18.3451896, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3631956185", "Coco cafe", "Cafe", "", "", 43.8338834, 18.3452861, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3762360454", "Barometar", "Cafe", "", "", 43.8575398, 18.4209679, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3784203144", "Cafe Firma", "Cafe", "", "", 43.8224052, 18.3674167, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3784216276", "Contra", "Cafe", "", "", 43.8224458, 18.3673175, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-3862534889", "Caffe Paris", "Cafe", "", "", 43.9019599, 18.3406041, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4019761726", "Cream Shop", "Cafe", "", "", 43.8591233, 18.4307593, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4206744903", "Caffe LoLeMi", "Cafe", "", "", 43.8482019, 18.3863947, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4206840695", "Caffe Kiss", "Cafe", "", "", 43.8512699, 18.3745509, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4238990996", "Torte i To", "Cafe", "", "", 43.8595887, 18.4282114, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4250033189", "Torte i To", "Cafe", "", "", 43.8580769, 18.416476, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4285066936", "Cafe Milano", "Cafe", "", "", 43.8205515, 18.2074446, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4306153803", "Caffe \"Biblioteka\"", "Cafe", "", "", 43.8536147, 18.3825555, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4350766604", "Caffe Bar Havana", "Cafe", "", "", 43.8585546, 18.4298825, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4383029890", "Ramis", "Cafe", "", "", 43.8593773, 18.4281988, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4383171290", "Demirović", "Cafe", "", "", 43.8593637, 18.4281003, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4383173289", "Slatko Ćoše", "Cafe", "", "", 43.8592952, 18.4281968, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4385011992", "Manolo", "Cafe", "", "", 43.8589428, 18.4192153, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4424350193", "Caffe bar", "Cafe", "", "", 43.8247122, 18.3501411, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4424352890", "Caffe bar \"Džan\"", "Cafe", "", "", 43.8264404, 18.3482188, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4447843218", "Mrvica", "Cafe", "", "", 43.8534988, 18.371781, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4457468892", "Đulagin dvor", "Cafe", "", "", 43.88042, 18.3991154, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4463793090", "NOOVA", "Cafe", "", "", 43.8420052, 18.4148374, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4565219253", "Izlog", "Cafe", "", "", 43.8203333, 18.3638031, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4579359292", "Hookah bar DEJA VU", "Cafe", "", "", 43.8509119, 18.3730216, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4642790184", "Coffee O Clock", "Cafe", "", "", 43.8579514, 18.4253414, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4689152733", "Caffe & Pizzeria HAPPY", "Cafe", "", "", 43.8489196, 18.384131, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4796996136", "Sova", "Cafe", "", "", 43.8578809, 18.4048148, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4833131019", "Caffe von Habsburg", "Cafe", "", "", 43.8577982, 18.4198372, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4887821525", "Promaha - kod Dragana", "Cafe", "", "", 43.9344216, 18.4236386, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4896938921", "Mr & Mrs Cue", "Cafe", "", "", 43.8540746, 18.3979914, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4935314126", "Sova caffé bar", "Cafe", "", "", 43.8583287, 18.4297931, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-4978636721", "Čajdžinica Džirlo", "Cafe", "", "", 43.8601786, 18.432098, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5073459496", "Cafe Bar Forza", "Cafe", "", "", 43.8524931, 18.3452562, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5136995540", "Aksaraj", "Cafe", "", "", 43.859352, 18.4309396, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5150549047", "Opera caffe bar", "Cafe", "", "", 43.8575938, 18.4213096, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5183803683", "Caffe Grad", "Cafe", "", "", 43.8591814, 18.4020977, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5185742050", "Caffe bar BarSA", "Cafe", "", "", 43.8198938, 18.3648572, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5203905647", "Rajvosa", "Cafe", "", "", 43.8582372, 18.4210028, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5223253222", "Coffee Shop Minasa", "Cafe", "", "", 43.8590128, 18.4319764, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5223393141", "Dia", "Cafe", "", "", 43.8593244, 18.4246386, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5228716077", "Theatre Cafe", "Cafe", "", "", 43.8591934, 18.4206243, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5246969911", "Caffe Kamarija", "Cafe", "", "", 43.8609914, 18.4385894, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5305441004", "Caffe Felicita", "Cafe", "", "", 43.8579274, 18.4087327, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5310352726", "Carigrad", "Cafe", "", "", 43.8593535, 18.4307817, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5313596801", "Casa del Caffe", "Cafe", "", "", 43.8565175, 18.4060492, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5313596806", "Marcaffe", "Cafe", "", "", 43.8564337, 18.4060492, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5319781938", "Tranzit", "Cafe", "", "", 43.9477195, 18.2665297, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5345042580", "Čeka", "Cafe", "", "", 43.858698, 18.4198704, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5528752841", "Saraj", "Cafe", "", "", 43.8597979, 18.4316193, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5536154266", "Hippy Klupa", "Cafe", "", "", 43.8591928, 18.4171201, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5557386931", "ANDALUZIJA", "Cafe", "", "", 43.8482324, 18.367483, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5581282221", "Art Kuca Sevdaha", "Cafe", "", "", 43.8586895, 18.4310017, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5709336625", "Penny Cafe", "Cafe", "", "", 43.865749, 18.4030329, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5739085929", "Ministry of Ćejf", "Cafe", "", "", 43.8555444, 18.4095133, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5743675476", "Giannini", "Cafe", "", "", 43.8548957, 18.4140663, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5784712838", "New Folder", "Cafe", "", "", 43.8270377, 18.3665656, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5825439453", "August Cafe", "Cafe", "", "", 43.8585808, 18.4192255, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5840301785", "Baklava Shop", "Cafe", "", "", 43.8589515, 18.4312714, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-5939106291", "Andar", "Cafe", "", "", 43.8594124, 18.4293789, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-6097099285", "MyFace Cafe", "Cafe", "", "", 43.8539692, 18.3972697, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-6130474349", "Uno momento", "Cafe", "", "", 43.8172286, 18.3622366, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-6175095870", "Hookah Bar Čiko", "Cafe", "", "", 43.8684082, 18.4077522, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-6362427023", "CAFFE TWINS", "Cafe", "", "", 43.8384309, 18.345881, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-6483125291", "Zdravo", "Cafe", "", "", 43.850811, 18.3563118, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-6612102578", "Caffe bar POINT", "Cafe", "", "", 43.8592626, 18.4183371, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-6618524309", "Caffe Elefant", "Cafe", "", "", 43.8181287, 18.3627933, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-6778902085", "Višnjik", "Cafe", "", "", 43.8667261, 18.4194366, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-6840057709", "Talks & Giggles", "Cafe", "", "", 43.8565194, 18.4255113, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-6845687985", "Old School Cafe", "Cafe", "", "", 43.8584366, 18.4184391, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-7412194049", "Avlija", "Cafe", "", "", 43.7994827, 18.3125681, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-7412194050", "Old Bar", "Cafe", "", "", 43.7980917, 18.3119808, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-7711403985", "Caffe Zrno", "Cafe", "", "", 43.8696029, 18.4179087, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-7925772814", "Caffe Slastičarna Badem", "Cafe", "", "", 43.8593655, 18.4278326, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-8137688077", "Vatra", "Cafe", "", "", 43.850601, 18.3533338, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-8141478626", "Klik", "Cafe", "", "", 43.8544531, 18.391914, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-8422060344", "City Walk", "Cafe", "", "", 43.859396, 18.4256333, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-8422060345", "Cafe Restaurant Revolucija 1764", "Cafe", "", "", 43.858728, 18.4231665, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-8914317017", "Caribou Coffee", "Cafe", "", "", 43.8529857, 18.3896351, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-9294750644", "R Sport Centar", "Cafe", "", "", 43.8759985, 18.4121859, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-9380556809", "Juice & Smoothies", "Cafe", "", "", 43.8526861, 18.4036757, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-9452711004", "Hookah Bar \"The Crown\"", "Cafe", "", "", 43.8250338, 18.3571114, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-9551683284", "Caffe & Snack bar KVART", "Cafe", "", "", 43.8533943, 18.3814856, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-9655798522", "Hookah Lounge Grace", "Cafe", "", "", 43.8589759, 18.4286707, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-9664492039", "Asta Cafe&Restaurant", "Cafe", "", "", 43.85827, 18.4267035, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-9790364217", "Fabrika", "Cafe", "", "", 43.859123, 18.4204693, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-9909805826", "Divan", "Cafe", "", "", 43.8596105, 18.4299466, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-9942892045", "Mona Seraa", "Cafe", "", "", 43.853743, 18.3972805, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-9946479868", "Poslastičarna Solun", "Cafe", "", "", 43.824184, 18.3541023, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10031990917", "Central Café", "Cafe", "", "", 43.8581192, 18.4266486, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10068912990", "M CAFFE", "Cafe", "", "", 43.8471539, 18.2414192, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10263490001", "GoForPixy 3", "Cafe", "", "", 43.8192907, 18.3536115, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10263573609", "GoForPixy 2", "Cafe", "", "", 43.8194488, 18.3533901, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10285720111", "Caffe bar Coshe", "Cafe", "", "", 43.8658128, 18.4220061, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10565803201", "Caribou Coffee", "Cafe", "", "", 43.8295248, 18.3100859, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10565803202", "Caribou Coffee", "Cafe", "", "", 43.8561688, 18.4072187, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10601082906", "Caffe  \"City bar\"", "Cafe", "", "", 43.8657367, 18.4097788, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10601138405", "Caffe bar \"Que Passa\"", "Cafe", "", "", 43.8615428, 18.4166108, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10619841105", "Cafe CapCarap", "Cafe", "", "", 43.8495766, 18.3984787, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10675912529", "Andalucia", "Cafe", "", "", 43.8573684, 18.3850879, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10702980676", "D-Moll", "Cafe", "", "", 43.8292661, 18.3111612, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10842253005", "Kawa", "Cafe", "", "", 43.8601572, 18.4230299, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-10958552823", "Milk-Bar", "Cafe", "", "", 43.8636265, 18.4147329, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11026900187", "Argelini", "Cafe", "", "", 43.8514933, 18.3801902, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11026943921", "Orient", "Cafe", "", "", 43.8523748, 18.3782324, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11026943924", "Mon Cheri", "Cafe", "", "", 43.851725, 18.3776547, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11039773890", "coffee2go", "Cafe", "", "", 43.8400901, 18.4501505, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11091090906", "So I čokolada", "Cafe", "", "", 43.8558521, 18.4206104, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11159424025", "Lateral", "Cafe", "", "", 43.8398316, 18.3231235, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11253112710", "Hotel Europe", "Cafe", "", "", 43.8586591, 18.4277115, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11253112724", "Carlsberg Beer Garden", "Cafe", "", "", 43.8519337, 18.3883001, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11414257098", "Ma Lu", "Cafe", "", "", 43.8169324, 18.3639333, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11553891888", "Mado", "Cafe", "", "", 43.830532, 18.3003156, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11951273319", "Mauricius", "Cafe", "", "", 43.8600237, 18.399076, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-11954147280", "Since 2K23 Concept", "Cafe", "", "", 43.8528768, 18.3994261, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12037315017", "BA", "Cafe", "", "", 43.8588634, 18.3968191, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12084006454", "Cafe bar Fratelli", "Cafe", "", "", 43.9225642, 18.317225, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12088763303", "Cordoba Cafe", "Cafe", "", "", 43.8582481, 18.4162354, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12357784014", "habitus", "Cafe", "", "", 43.8598169, 18.4184523, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12395846001", "Caffe R", "Cafe", "", "", 43.8554389, 18.4151968, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12631327194", "Corner", "Cafe", "", "", 43.8163081, 18.3096546, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12631327195", "imperial", "Cafe", "", "", 43.8153573, 18.312547, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12640565285", "Caribou Coffee", "Cafe", "", "", 43.8305764, 18.300662, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12771927631", "Barcelona", "Cafe", "", "", 43.8385102, 18.3463287, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12902327709", "Vanila", "Cafe", "", "", 43.8237584, 18.3617004, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12990027680", "Djul Café", "Cafe", "", "", 43.8587317, 18.4325343, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12990117802", "Kafa Šehar", "Cafe", "", "", 43.8589355, 18.4321007, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-12990117804", "Aksaraj", "Cafe", "", "", 43.8590238, 18.4324246, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13086948697", "Juice & coffee Life bar", "Cafe", "", "", 43.8502053, 18.3628579, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13086959008", "Caffe Avalon", "Cafe", "", "", 43.8501864, 18.3627041, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13114224754", "Озеро", "Cafe", "", "", 43.8163463, 18.3665318, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13192278307", "Fuka", "Cafe", "", "", 43.858696, 18.4225643, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13238540848", "Dolar", "Cafe", "", "", 43.8683157, 18.4056948, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13249441072", "Fabrika", "Cafe", "", "", 43.8593003, 18.4205374, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13249590747", "Funky Chef", "Cafe", "", "", 43.8571378, 18.4172765, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13249590748", "Cordoba", "Cafe", "", "", 43.8584933, 18.4170692, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13410043655", "Laheri Book Caffe", "Cafe", "", "", 43.8586331, 18.4308476, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13418753994", "Caffe Slastičarna Centar", "Cafe", "", "", 43.8219217, 18.2015254, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13622222101", "Ayna", "Cafe", "", "", 43.8593481, 18.4303429, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13624541502", "Caffe Putnik", "Cafe", "", "", 43.8236402, 18.3565677, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13624573302", "Caffe SN", "Cafe", "", "", 43.8238322, 18.3561973, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13704272799", "Le.Sal+Co.", "Cafe", "", "", 43.8578836, 18.4041467, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13901427512", "Diverzija", "Cafe", "", "", 43.8349892, 18.3283235, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-13908979873", "Brčko gas", "Cafe", "", "", 43.8160582, 18.3609651, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-14075691035", "Hookah Bar Barista", "Cafe", "", "", 43.8227181, 18.2064874, "OSM cafe in Sarajevo.", 0.0),
            PlaceSeed("osm-cafe-14099010101", "Franck Experience Store Sarajevo", "Cafe", "", "", 43.8583291, 18.4256711, "OSM cafe in Sarajevo.", 0.0)
        )

        osmCafes.forEach { place ->
            val exists = Places
                .select(Places.id)
                .where { Places.id eq place.id }
                .count() > 0

            if (!exists) {
                Places.insert {
                    it[Places.id] = place.id
                    it[Places.name] = place.name
                    it[Places.category] = place.category
                    it[Places.venueId] = null
                    it[Places.address] = place.address
                    it[Places.latitude] = place.latitude
                    it[Places.longitude] = place.longitude
                    it[Places.description] = place.description
                    it[Places.imageUrl] = null
                    it[Places.rating] = place.rating
                    it[Places.createdAt] = Instant.now()
                }
            }
        }

        println("Seeded places.")
    }



    private fun seedBusStations() {
        val stations = listOf(
            BusStationSeed("seed-station-01", "Baščaršija", 43.8609, 18.4291, "1,2,3"),
            BusStationSeed("seed-station-02", "Vijećnica", 43.8594, 18.4334, "1,2,3"),
            BusStationSeed("seed-station-03", "Skenderija", 43.8534, 18.4078, "2,3,5"),
            BusStationSeed("seed-station-04", "Marijin Dvor", 43.8563, 18.4107, "2,3,5,6"),
            BusStationSeed("seed-station-05", "Čengić Vila", 43.8448, 18.3651, "3,4,5"),
            BusStationSeed("seed-station-06", "Otoka", 43.8464, 18.3669, "3,4,5"),
            BusStationSeed("seed-station-07", "Stup", 43.8397, 18.3217, "3,4,6"),
            BusStationSeed("seed-station-08", "Ilidža", 43.8295, 18.3109, "3,4,6"),
            BusStationSeed("seed-station-09", "Pofalići", 43.8545, 18.3897, "2,3,5"),
            BusStationSeed("seed-station-10", "Koševo", 43.8701, 18.4074, "14,16"),
            BusStationSeed("seed-station-11", "Ciglane", 43.8653, 18.4071, "14,16"),
            BusStationSeed("seed-station-12", "Bistrik", 43.8547, 18.4298, "1,2")
        )

        stations.forEach { station ->
            val exists = BusStations
                .select(BusStations.id)
                .where { BusStations.id eq station.id }
                .count() > 0

            if (!exists) {
                BusStations.insert {
                    it[BusStations.id] = station.id
                    it[BusStations.name] = station.name
                    it[BusStations.latitude] = station.latitude
                    it[BusStations.longitude] = station.longitude
                    it[BusStations.lines] = station.lines
                    it[BusStations.createdAt] = Instant.now()
                }
            }
        }

        println("Seeded bus stations.")
    }

    private fun seedPosts() {
        val posts = listOf(
            PostSeed("seed-post-01", SEED_USER_01, "Morning coffee and Baščaršija streets. Sarajevo never gets old. ☕"),
            PostSeed("seed-post-02", SEED_USER_02, "Perfect weather for a walk around the city today. 🌤️"),
            PostSeed("seed-post-03", SEED_USER_03, "Trying a new outfit for this rainy Sarajevo afternoon. 🌧️"),
            PostSeed("seed-post-04", SEED_USER_04, "Sunset from Trebević was absolutely worth the climb. 🌅"),
            PostSeed("seed-post-05", SEED_USER_05, "Vijećnica looks incredible in the evening. 📸")
        )

        posts.forEach { post ->
            val exists = Posts
                .select(Posts.id)
                .where { Posts.id eq post.id }
                .count() > 0

            if (!exists) {
                Posts.insert {
                    it[Posts.id] = post.id
                    it[Posts.userId] = post.userId
                    it[Posts.caption] = post.caption
                    it[Posts.imageUrl] = null
                    it[Posts.createdAt] = Instant.now()
                }
            }
        }

        println("Seeded posts.")
    }

    private data class UserSeed(
        val id: String,
        val email: String,
        val username: String,
        val bio: String,
        val favoriteLocation: String
    )

    private data class LocationSeed(
        val id: String,
        val name: String,
        val description: String,
        val latitude: Double,
        val longitude: Double
    )

    private data class MusicSeed(
        val id: String,
        val title: String,
        val artist: String,
        val duration: Int
    )

    private data class PlaylistSeed(
        val id: String,
        val title: String,
        val genre: String,
        val mood: String,
        val weather: String,
        val temperature: String,
        val location: String,
        val bestFor: String,
        val likes: Int,
        val youtubeUrl: String
    )

    private data class OutfitSeed(
        val id: String,
        val userId: String,
        val title: String,
        val weatherCondition: String,
        val season: String,
        val storeName: String,
        val storeAddress: String,
        val price: String,
        val likes: Int,
        val imageUrl: String,
        val storePhone: String?,
        val productUrl: String?
    )

    private data class PlaceSeed(
        val id: String,
        val name: String,
        val category: String,
        val venueName: String,
        val address: String,
        val latitude: Double,
        val longitude: Double,
        val description: String,
        val rating: Double
    )

    private data class RecommendationSeed(
        val id: String,
        val name: String,
        val category: String,
        val location: String,
        val placeId: String,
        val description: String,
        val suitableFor: String,
        val weatherCondition: String,
        val ageGroup: String,
        val rating: Double
    )

    private data class BusStationSeed(
        val id: String,
        val name: String,
        val latitude: Double,
        val longitude: Double,
        val lines: String
    )

    private data class PostSeed(
        val id: String,
        val userId: String,
        val caption: String
    )
}