package com.example.database.seed

import com.example.music.MusicRepository
import com.example.playlist.PlaylistRepository
import kotlin.uuid.Uuid

object SeedData {

    fun seed(
        musicRepository: MusicRepository,
        playlistRepository: PlaylistRepository
    ) {

        if (playlistRepository.getPlaylists().isNotEmpty()) {
            println("Seed data already exists. Skipping.")
            return
        }

        println("Seeding music and playlist data...")

        val coffeeTime = musicRepository.createMusic(
            title = "Coffee Time",
            artist = "Example Artist",
            duration = "3:45",
            albumImageUrl = null
        )

        val morningWalk = musicRepository.createMusic(
            title = "Morning Walk",
            artist = "Example Artist",
            duration = "4:10",
            albumImageUrl = null,
        )

        val rainyDay = musicRepository.createMusic(
            title = "Rainy Day",
            artist = "Example Artist",
            duration = "3:52",
            albumImageUrl = null,
        )

        val lofiStudy = musicRepository.createMusic(
            title = "Lofi Study",
            artist = "Example Artist",
            duration = "3:28",
            albumImageUrl = null
        )

        val nightDrive = musicRepository.createMusic(
            title = "Night Drive",
            artist = "Example Artist",
            duration = "4:02",
            albumImageUrl = null
        )

        val summerVibes = musicRepository.createMusic(
            title = "Summer Vibes",
            artist = "Example Artist",
            duration = "3:35",
            albumImageUrl = null
        )

        val hipHopEnergy = musicRepository.createMusic(
            title = "Energy",
            artist = "Example Artist",
            duration = "3:18",
            albumImageUrl = null
        )

        val cozyEvening = musicRepository.createMusic(
            title = "Cozy Evening",
            artist = "Example Artist",
            duration = "4:12",
            albumImageUrl = null
        )

        val autumnWalk = musicRepository.createMusic(
            title = "Autumn Walk",
            artist = "Example Artist",
            duration = "3:47",
            albumImageUrl = null
        )

        val lateNight = musicRepository.createMusic(
            title = "Late Night",
            artist = "Example Artist",
            duration = "4:21",
            albumImageUrl = null
        )

        val weekend = musicRepository.createMusic(
            title = "Weekend",
            artist = "Example Artist",
            duration = "3:31",
            albumImageUrl = null
        )

        val peaceful = musicRepository.createMusic(
            title = "Peaceful",
            artist = "Example Artist",
            duration = "4:05",
            albumImageUrl = null
        )

        val goldenHour = musicRepository.createMusic(
            title = "Golden Hour",
            artist = "Example Artist",
            duration = "3:46",
            albumImageUrl = null
        )

        val deepFocus = musicRepository.createMusic(
            title = "Deep Focus",
            artist = "Example Artist",
            duration = "5:02",
            albumImageUrl = null
        )

        val chillNight = musicRepository.createMusic(
            title = "Chill Night",
            artist = "Example Artist",
            duration = "3:58",
            albumImageUrl = null
        )

        val jazzEvening = musicRepository.createMusic(
            title = "Evening Jazz",
            artist = "Example Artist",
            duration = "4:17",
            albumImageUrl = null
        )

        val popEnergy = musicRepository.createMusic(
            title = "Pop Energy",
            artist = "Example Artist",
            duration = "3:22",
            albumImageUrl = null
        )

        val winterCoffee = musicRepository.createMusic(
            title = "Winter Coffee",
            artist = "Example Artist",
            duration = "4:08",
            albumImageUrl = null
        )

        val rainyWalk = musicRepository.createMusic(
            title = "Rainy Walk",
            artist = "Example Artist",
            duration = "3:54",
            albumImageUrl = null
        )

        val midnight = musicRepository.createMusic(
            title = "Midnight",
            artist = "Example Artist",
            duration = "4:26",
            albumImageUrl = null
        )

        val playlist1 = playlistRepository.createPlaylist(
            title = "Morning Coffee",
            genre = "Jazz",
            mood = "Relaxed",
            albumImageUrl = null,
            weather = "Sunny",
            temperature = "22°C",
            location = "Baščaršija",
            spotifyUrl = null,
            youtubeUrl = null
        )

        val playlist2 = playlistRepository.createPlaylist(
            title = "Rainy Day",
            genre = "Indie",
            mood = "Cozy",
            albumImageUrl = null,
            weather = "Rainy",
            temperature = "15°C",
            location = "Baščaršija",
            spotifyUrl = null,
            youtubeUrl = null
        )

        val playlist3 = playlistRepository.createPlaylist(
            title = "Lofi Study",
            genre = "Lo-Fi",
            mood = "Focused",
            albumImageUrl = null,
            weather = "Cloudy",
            temperature = "17°C",
            location = "Otoka",
            spotifyUrl = null,
            youtubeUrl = null
        )

        val playlist4 = playlistRepository.createPlaylist(
            title = "Night Drive",
            genre = "Electronic",
            mood = "Chill",
            albumImageUrl = null,
            weather = "Clear",
            temperature = "18°C",
            location = "Sarajevo",
            spotifyUrl = null,
            youtubeUrl = null
        )

        val playlist5 = playlistRepository.createPlaylist(
            title = "Hip-Hop Workout",
            genre = "Hip-Hop",
            mood = "Energetic",
            albumImageUrl = null,
            weather = "Sunny",
            temperature = "24°C",
            location = "Sarajevo",
            spotifyUrl = null,
            youtubeUrl = null
        )

        val playlist6 = playlistRepository.createPlaylist(
            title = "Cozy Evening",
            genre = "R&B",
            mood = "Calm",
            albumImageUrl = null,
            weather = "Cloudy",
            temperature = "16°C",
            location = "Vogošća",
            spotifyUrl = null,
            youtubeUrl = null
        )

        val playlist7 = playlistRepository.createPlaylist(
            title = "Summer Vibes",
            genre = "Pop",
            mood = "Happy",
            albumImageUrl = null,
            weather = "Sunny",
            temperature = "28°C",
            location = "Ilidža",
            spotifyUrl = null,
            youtubeUrl = null
        )

        val playlist8 = playlistRepository.createPlaylist(
            title = "Late Night Jazz",
            genre = "Jazz",
            mood = "Moody",
            albumImageUrl = null,
            weather = "Clear",
            temperature = "14°C",
            location = "Sarajevo",
            spotifyUrl = null,
            youtubeUrl = null
        )

        val playlist9 = playlistRepository.createPlaylist(
            title = "Autumn Walk",
            genre = "Indie",
            mood = "Peaceful",
            albumImageUrl = null,
            weather = "Cloudy",
            temperature = "12°C",
            location = "Koševo",
            spotifyUrl = null,
            youtubeUrl = null
        )

        val playlist10 = playlistRepository.createPlaylist(
            title = "Weekend Energy",
            genre = "Pop",
            mood = "Energetic",
            albumImageUrl = null,
            weather = "Sunny",
            temperature = "23°C",
            location = "Baščaršija",
            spotifyUrl = null,
            youtubeUrl = null
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist1.id),
            Uuid.parse(coffeeTime.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist1.id),
            Uuid.parse(morningWalk.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist1.id),
            Uuid.parse(winterCoffee.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist2.id),
            Uuid.parse(rainyDay.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist2.id),
            Uuid.parse(rainyWalk.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist2.id),
            Uuid.parse(peaceful.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist3.id),
            Uuid.parse(lofiStudy.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist3.id),
            Uuid.parse(deepFocus.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist3.id),
            Uuid.parse(peaceful.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist4.id),
            Uuid.parse(nightDrive.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist4.id),
            Uuid.parse(chillNight.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist4.id),
            Uuid.parse(midnight.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist5.id),
            Uuid.parse(hipHopEnergy.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist5.id),
            Uuid.parse(popEnergy.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist5.id),
            Uuid.parse(weekend.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist6.id),
            Uuid.parse(cozyEvening.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist6.id),
            Uuid.parse(chillNight.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist6.id),
            Uuid.parse(jazzEvening.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist7.id),
            Uuid.parse(summerVibes.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist7.id),
            Uuid.parse(goldenHour.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist7.id),
            Uuid.parse(popEnergy.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist8.id),
            Uuid.parse(jazzEvening.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist8.id),
            Uuid.parse(lateNight.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist8.id),
            Uuid.parse(midnight.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist9.id),
            Uuid.parse(autumnWalk.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist9.id),
            Uuid.parse(peaceful.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist9.id),
            Uuid.parse(rainyWalk.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist10.id),
            Uuid.parse(weekend.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist10.id),
            Uuid.parse(summerVibes.id)
        )

        playlistRepository.addSongToPlaylist(
            Uuid.parse(playlist10.id),
            Uuid.parse(goldenHour.id)
        )

        println("Music and playlist seed completed!")
    }
}