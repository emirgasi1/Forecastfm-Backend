package com.example.database.seed

import com.example.database.table.Playlists
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

object PlaylistSeeder {

    fun seedBestForTags() {
        transaction {
            println("Checking for playlists missing 'bestFor' tags...")

            // 1. Find all playlists where bestFor is null or blank
            val playlistsToUpdate = Playlists
                .selectAll()
                .where {
                    (Playlists.bestFor.isNull()) or (Playlists.bestFor eq "")
                }
                .toList()

            if (playlistsToUpdate.isEmpty()) {
                println("All playlists already have bestFor tags. Skipping.")
                return@transaction
            }

            var updatedCount = 0

            // 2. Loop through them and apply tags based on their existing fields
            playlistsToUpdate.forEach { row ->
                val id = row[Playlists.id]
                val genre = row[Playlists.genre].lowercase()
                val mood = row[Playlists.mood].lowercase()
                val weather = row[Playlists.weather].lowercase()
                val title = row[Playlists.title].lowercase()

                // Build the tag list dynamically based on the playlist's data
                val tags = mutableSetOf<String>()

                // --- Weather Tags ---
                if (weather.contains("sun") || weather.contains("clear")) {
                    tags.addAll(listOf("sunny", "clear"))
                } else if (weather.contains("rain") || weather.contains("drizzle")) {
                    tags.addAll(listOf("rainy", "rain"))
                } else if (weather.contains("snow")) {
                    tags.addAll(listOf("snow", "winter", "cold"))
                } else if (weather.contains("cloud") || weather.contains("overcast")) {
                    tags.addAll(listOf("cloudy", "chill"))
                }

                // --- Mood Tags ---
                if (mood.contains("chill") || mood.contains("relax") || mood.contains("calm")) {
                    tags.addAll(listOf("chill", "relaxed", "evening"))
                } else if (mood.contains("energ") || mood.contains("happy") || mood.contains("upbeat")) {
                    tags.addAll(listOf("upbeat", "energetic", "morning", "afternoon"))
                } else if (mood.contains("sad") || mood.contains("melo")) {
                    tags.addAll(listOf("melancholy", "night", "rain"))
                } else if (mood.contains("focus") || mood.contains("study")) {
                    tags.addAll(listOf("focus", "morning", "cloudy"))
                }

                // --- Genre Tags ---
                if (genre.contains("jazz")) {
                    tags.addAll(listOf("morning", "evening", "acoustic"))
                } else if (genre.contains("lo-fi") || genre.contains("lofi")) {
                    tags.addAll(listOf("night", "chill", "focus"))
                } else if (genre.contains("rock")) {
                    tags.addAll(listOf("energetic", "afternoon"))
                } else if (genre.contains("pop")) {
                    tags.addAll(listOf("upbeat", "afternoon"))
                } else if (genre.contains("ambient")) {
                    tags.addAll(listOf("night", "dreamy", "chill"))
                } else if (genre.contains("acoustic") || genre.contains("indie")) {
                    tags.addAll(listOf("morning", "evening", "chill"))
                }

                // --- Circadian / Time of day fallbacks ---
                if (title.contains("morning")) tags.add("morning")
                if (title.contains("night") || title.contains("midnight")) tags.add("night")
                if (title.contains("sunset") || title.contains("evening")) tags.add("evening")
                if (title.contains("workout") || title.contains("gym")) tags.add("afternoon")

                // If we somehow didn't generate any tags, give it a neutral default
                if (tags.isEmpty()) {
                    tags.addAll(listOf("chill", "neutral"))
                }

                // 3. Update the database row
                val bestForString = tags.joinToString(",")

                Playlists.update({ Playlists.id eq id }) {
                    it[Playlists.bestFor] = bestForString
                }

                updatedCount++
            }

            println("Successfully generated and applied 'bestFor' tags to $updatedCount playlists.")
        }
    }
}