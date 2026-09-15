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
                seedPlaceRecommendations()
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
            UserSeed(
                SEED_USER_01,
                "amina.kovac@forecastfm.demo",
                "amina_kovac",
                "Coffee, sunsets and Sarajevo walks.",
                "Baščaršija"
            ),
            UserSeed(
                SEED_USER_02,
                "adnan.hadzic@forecastfm.demo",
                "adnan_hadzic",
                "Music, food and exploring new places.",
                "Marijin Dvor"
            ),
            UserSeed(
                SEED_USER_03,
                "lejla.basic@forecastfm.demo",
                "lejla_basic",
                "Finding the perfect outfit for every weather.",
                "Ilidža"
            ),
            UserSeed(
                SEED_USER_04,
                "dino.memisevic@forecastfm.demo",
                "dino_m",
                "Always looking for the next good playlist.",
                "Trebević"
            ),
            UserSeed(
                SEED_USER_05,
                "sara.kovacevic@forecastfm.demo",
                "sara_k",
                "Sarajevo through my camera.",
                "Vijećnica"
            )
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
        val locations = listOf(
            LocationSeed("seed-location-bascarsija", "Baščaršija", "Stari gradski trg i historijsko srce Sarajeva.", 43.8608, 18.4288),
            LocationSeed("seed-location-vrelo-bosne", "Vrelo Bosne", "Prirodni park na izvoru rijeke Bosne.", 43.8196, 18.2695),
            LocationSeed("seed-location-avaz", "Avaz Twist Tower", "Jedan od najpoznatijih simbola modernog Sarajeva.", 43.8584, 18.4055),
            LocationSeed("seed-location-bijela-tabija", "Bijela Tabija", "Historijska tvrđava sa panoramskim pogledom na Sarajevo.", 43.8620, 18.4462),
            LocationSeed("seed-location-katedrala", "Katedrala Srca Isusova", "Poznata katedrala u centru Sarajeva.", 43.8593, 18.4248),
            LocationSeed("seed-location-sebilj", "Sebilj - Baščaršija", "Jedan od najpoznatijih simbola Sarajeva.", 43.8607, 18.4290),
            LocationSeed("seed-location-zmajevac", "Zmajevac", "Popularno mjesto sa pogledom na Sarajevo.", 43.8672, 18.4122),
            LocationSeed("seed-location-vijecnica", "Vijećnica", "Poznata historijska građevina uz Miljacku.", 43.8595, 18.4331),
            LocationSeed("seed-location-trebevic", "Trebević", "Planina iznad Sarajeva idealna za prirodu i šetnju.", 43.8271, 18.4485),
            LocationSeed("seed-location-ilidza", "Ilidža", "Područje poznato po parkovima i prirodi.", 43.8297, 18.3118),
            LocationSeed("seed-location-marijin-dvor", "Marijin Dvor", "Centralni dio Sarajeva sa urbanom atmosferom.", 43.8564, 18.4105),
            LocationSeed("seed-location-skenderija", "Skenderija", "Urbano područje uz Miljacku.", 43.8535, 18.4078),
            LocationSeed("seed-location-kosevo", "Koševo", "Gradsko područje sa zelenim površinama.", 43.8702, 18.4075),
            LocationSeed("seed-location-ciglane", "Ciglane", "Naselje na padinama Sarajeva.", 43.8654, 18.4070),
            LocationSeed("seed-location-pofalici", "Pofalići", "Urbano područje zapadno od centra.", 43.8546, 18.3898),
            LocationSeed("seed-location-otoka", "Otoka", "Živahno gradsko područje sa sportskim sadržajima.", 43.8465, 18.3667),
            LocationSeed("seed-location-cengic-vila", "Čengić Vila", "Popularno gradsko naselje povezano tramvajem.", 43.8447, 18.3650),
            LocationSeed("seed-location-stup", "Stup", "Važno urbano i saobraćajno područje.", 43.8397, 18.3218),
            LocationSeed("seed-location-bistrik", "Bistrik", "Historijsko sarajevsko naselje.", 43.8547, 18.4298),
            LocationSeed("seed-location-vraca", "Vraca", "Područje na južnim padinama Sarajeva.", 43.8448, 18.3935)
        )

        locations.forEach { location ->
            val idExists = Locations
                .select(Locations.id)
                .where { Locations.id eq location.id }
                .count() > 0

            val nameExists = Locations
                .select(Locations.id)
                .where { Locations.name eq location.name }
                .count() > 0

            if (!idExists && !nameExists) {
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
            PlaylistSeed(
                "seed-playlist-morning",
                "Sarajevo Morning",
                "Indie Pop",
                "Chill",
                "Clear",
                "15-22°C",
                "Sarajevo",
                "Alone,Coffee,Work",
                18,
                "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"
            ),
            PlaylistSeed(
                "seed-playlist-sunny",
                "Sunny Sarajevo",
                "Pop",
                "Feel Good",
                "Sunny",
                "20-30°C",
                "Sarajevo",
                "Friends,Outdoor,Day Out",
                34,
                "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"
            ),
            PlaylistSeed(
                "seed-playlist-rainy",
                "Rainy Baščaršija",
                "Indie",
                "Cozy",
                "Rain",
                "8-18°C",
                "Baščaršija",
                "Alone,Couple,Coffee",
                27,
                "https://www.youtube.com/watch?v=j7X3vq6GY2c"
            ),
            PlaylistSeed(
                "seed-playlist-sunset",
                "Sarajevo Sunset",
                "R&B",
                "Romantic",
                "Cloudy",
                "12-24°C",
                "Trebević",
                "Couple,Date,Relax",
                41,
                "https://www.youtube.com/playlist?list=PL-zl0Qa3WDZ-XYzFHodllO2vHmE7kGODR"
            ),
            PlaylistSeed(
                "seed-playlist-night",
                "Sarajevo Night",
                "Pop",
                "Energetic",
                "Clear",
                "10-25°C",
                "Marijin Dvor",
                "Friends,Night Out,Party",
                56,
                "https://www.youtube.com/playlist?list=PLkShY3_KwgIIHz8OsRyAu2dTQcHg1dmWl"
            )
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
            "seed-playlist-night" to listOf("seed-music-16", "seed-music-17", "seed-music-18", "seed-music-24", "seed-music-25")
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
            OutfitSeed("seed-outfit-01", SEED_USER_01, "Sunny City Casual", "Sunny", "Summer", "Zara", "Maršala Tita, Sarajevo", "60-100 KM", 24),
            OutfitSeed("seed-outfit-02", SEED_USER_02, "Coffee & Chill", "Cloudy", "Spring", "LC Waikiki", "Bingo City Center, Sarajevo", "40-80 KM", 18),
            OutfitSeed("seed-outfit-03", SEED_USER_03, "Rainy Day Layers", "Rain", "Autumn", "Reserved", "Sarajevo City Center", "80-140 KM", 31),
            OutfitSeed("seed-outfit-04", SEED_USER_04, "Baščaršija Weekend", "Clear", "Summer", "Mango", "Sarajevo City Center", "90-150 KM", 42),
            OutfitSeed("seed-outfit-05", SEED_USER_05, "Trebević Adventure", "Cool", "Autumn", "Sport Vision", "Džidžikovac, Sarajevo", "120-220 KM", 27),
            OutfitSeed("seed-outfit-06", SEED_USER_01, "Winter Walk", "Snow", "Winter", "Sport Reality", "Stup, Sarajevo", "150-280 KM", 35),
            OutfitSeed("seed-outfit-07", SEED_USER_02, "Spring Afternoon", "Clear", "Spring", "H&M", "Sarajevo City Center", "70-130 KM", 21),
            OutfitSeed("seed-outfit-08", SEED_USER_03, "Evening Downtown", "Clear", "Autumn", "Pull&Bear", "Sarajevo City Center", "80-140 KM", 38),
            OutfitSeed("seed-outfit-09", SEED_USER_04, "Hot Summer Day", "Sunny", "Summer", "DeFacto", "Bingo City Center, Sarajevo", "40-90 KM", 29),
            OutfitSeed("seed-outfit-10", SEED_USER_05, "Cozy Evening", "Cold", "Winter", "Reserved", "Sarajevo City Center", "100-180 KM", 46)
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
                    it[Outfits.imageUrl] = ""
                    it[Outfits.title] = outfit.title
                    it[Outfits.weatherCondition] = outfit.weatherCondition
                    it[Outfits.season] = outfit.season
                    it[Outfits.likes] = outfit.likes
                    it[Outfits.createdAt] = Instant.now()
                    it[Outfits.storeName] = outfit.storeName
                    it[Outfits.storeAddress] = outfit.storeAddress
                    it[Outfits.price] = outfit.price
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

            val venue = Locations
                .select(Locations.id)
                .where { Locations.id eq place.venueName }
                .singleOrNull()

            if (venue != null) {
                val venueId = venue[Locations.id]

                val exists = Places
                    .select(Places.id)
                    .where { Places.id eq place.id }
                    .count() > 0

                if (!exists) {
                    Places.insert {
                        it[Places.id] = place.id
                        it[Places.name] = place.name
                        it[Places.category] = place.category
                        it[Places.venueId] = venueId
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
        }

        println("Seeded places.")
    }

    private fun seedPlaceRecommendations() {
        val recommendations = listOf(
            RecommendationSeed("seed-recommendation-01", "Morning Coffee in Baščaršija", "Cafe", "Baščaršija", "Start the morning with traditional coffee and a relaxed city atmosphere.", "alone,couple,friends", "clear,cloudy", "young,adult,senior", 4.6),
            RecommendationSeed("seed-recommendation-02", "Walk Through Vrelo Bosne", "Outdoor", "Ilidža", "A peaceful nature walk suitable for a relaxed day outdoors.", "alone,couple,friends,family", "sunny,cloudy", "child,young,adult,senior", 4.8),
            RecommendationSeed("seed-recommendation-03", "Trebević Adventure", "Outdoor", "Trebević", "Spend the afternoon in nature with panoramic views of Sarajevo.", "alone,couple,friends", "sunny,cloudy", "young,adult", 4.7),
            RecommendationSeed("seed-recommendation-04", "Romantic Sarajevo Sunset", "Viewpoint", "Zmajevac", "Enjoy a relaxed sunset with a panoramic view of Sarajevo.", "couple", "clear", "young,adult,senior", 4.8),
            RecommendationSeed("seed-recommendation-05", "Family Day at Ilidža", "Park", "Ilidža", "A relaxed outdoor activity for families with children.", "family", "sunny,cloudy", "child,young,adult", 4.6),
            RecommendationSeed("seed-recommendation-06", "Historic Baščaršija Walk", "Culture", "Baščaršija", "Explore historic streets, markets and landmarks.", "alone,couple,friends,family", "sunny,cloudy", "child,young,adult,senior", 4.9),
            RecommendationSeed("seed-recommendation-07", "Rainy Day Cafe", "Cafe", "Marijin Dvor", "A cozy indoor option for rainy weather.", "alone,couple,friends", "rain", "young,adult,senior", 4.4),
            RecommendationSeed("seed-recommendation-08", "Evening City Walk", "Outdoor", "Skenderija", "Take an easy evening walk beside the Miljacka.", "couple,friends", "clear,cloudy", "young,adult", 4.3),
            RecommendationSeed("seed-recommendation-09", "Coffee with a View", "Cafe", "Zmajevac", "Enjoy coffee while looking over the Sarajevo valley.", "alone,couple,friends", "clear", "young,adult,senior", 4.7),
            RecommendationSeed("seed-recommendation-10", "Winter City Walk", "Outdoor", "Bistrik", "Explore a historic Sarajevo neighbourhood during a cool day.", "alone,couple,friends", "snow,cold", "young,adult,senior", 4.4),
            RecommendationSeed("seed-recommendation-11", "Friends Afternoon", "Cafe", "Otoka", "Casual place for meeting friends during the afternoon.", "friends", "sunny,cloudy", "young,adult", 4.2),
            RecommendationSeed("seed-recommendation-12", "Family Weekend Walk", "Outdoor", "Koševo", "Easy outdoor activity for a relaxed family weekend.", "family", "sunny,cloudy", "child,young,adult", 4.3),
            RecommendationSeed("seed-recommendation-13", "Downtown Date", "Restaurant", "Marijin Dvor", "A simple downtown option for an evening date.", "couple", "clear,cloudy,rain", "young,adult", 4.5),
            RecommendationSeed("seed-recommendation-14", "Sunny City Exploration", "Culture", "Marijin Dvor", "Explore the modern side of Sarajevo on a sunny day.", "alone,couple,friends", "sunny", "young,adult", 4.4),
            RecommendationSeed("seed-recommendation-15", "Quiet Senior Afternoon", "Park", "Ilidža", "A calm environment for a slower afternoon outdoors.", "alone,couple,family", "sunny,cloudy", "adult,senior", 4.6)
        )

        recommendations.forEach { recommendation ->
            val exists = PlaceRecommendations
                .select(PlaceRecommendations.id)
                .where { PlaceRecommendations.id eq recommendation.id }
                .count() > 0

            if (!exists) {
                PlaceRecommendations.insert {
                    it[PlaceRecommendations.id] = recommendation.id
                    it[PlaceRecommendations.name] = recommendation.name
                    it[PlaceRecommendations.category] = recommendation.category
                    it[PlaceRecommendations.location] = recommendation.location
                    it[PlaceRecommendations.description] = recommendation.description
                    it[PlaceRecommendations.suitableFor] = recommendation.suitableFor
                    it[PlaceRecommendations.weatherCondition] = recommendation.weatherCondition
                    it[PlaceRecommendations.ageGroup] = recommendation.ageGroup
                    it[PlaceRecommendations.rating] = recommendation.rating
                    it[PlaceRecommendations.imageUrl] = null
                    it[PlaceRecommendations.createdAt] = Instant.now()
                }
            }
        }

        println("Seeded recommendations.")
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
            PostSeed(
                "seed-post-01",
                SEED_USER_01,
                "Morning coffee and Baščaršija streets. Sarajevo never gets old. ☕",
            ),
            PostSeed(
                "seed-post-02",
                SEED_USER_02,
                "Perfect weather for a walk around the city today. 🌤️",
            ),
            PostSeed(
                "seed-post-03",
                SEED_USER_03,
                "Trying a new outfit for this rainy Sarajevo afternoon. 🌧️",
            ),
            PostSeed(
                "seed-post-04",
                SEED_USER_04,
                "Sunset from Trebević was absolutely worth the climb. 🌅",
            ),
            PostSeed(
                "seed-post-05",
                SEED_USER_05,
                "Vijećnica looks incredible in the evening. 📸",
            )
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
        val likes: Int
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
