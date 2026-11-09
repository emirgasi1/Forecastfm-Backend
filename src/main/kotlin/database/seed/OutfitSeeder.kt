package com.example.database.seed

import com.example.database.table.Outfits
import database.table.Users
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.eq
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

object OutfitSeeder {

    fun seedIfEmpty() {
        transaction {
            val count = Outfits.selectAll().count()
            if (count > 0) {
                println("Outfits already seeded. Skipping.")
                return@transaction
            }

            println("Seeding curated outfits...")

            val systemUserId = "system_curated_outfits"

            // Ensure the system user exists to prevent Foreign Key errors
            val userExists = Users.selectAll().where { Users.id eq systemUserId }.count() > 0
            if (!userExists) {
                Users.insert {
                    it[id] = systemUserId
                    it[email] = "style@forecastfm.com"
                    it[username] = "ForecastFM Style"
                    it[passwordHash] = "not_a_real_password"
                    it[createdAt] = LocalDateTime.now()
                }
            }

            val outfits = listOf(
                // --- SUNNY (5) ---
                createOutfitData("Sunny", "Summer", "Linen Button-Down", "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=600&q=80"),
                createOutfitData("Sunny", "Summer", "Light Denim Shorts", "https://images.unsplash.com/photo-1591195853828-11db59a44f6b?w=600&q=80"),
                createOutfitData("Sunny", "Spring", "Floral Summer Dress", "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=600&q=80"),
                createOutfitData("Sunny", "Summer", "Classic White Sneakers", "https://images.unsplash.com/photo-1600269452121-4f2416e55c28?w=600&q=80"),
                createOutfitData("Sunny", "Summer", "Straw Sun Hat", "https://images.unsplash.com/photo-1521369909029-2afed882baee?w=600&q=80"),

                // --- PARTLY CLOUDY (5) ---
                createOutfitData("Partly Cloudy", "Spring", "Light Bomber Jacket", "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=600&q=80"),
                createOutfitData("Partly Cloudy", "Spring", "Casual Chinos", "https://images.unsplash.com/photo-1542272604-787c3835535d?w=600&q=80"),
                createOutfitData("Partly Cloudy", "Fall", "Long Sleeve Henley", "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=600&q=80"),
                createOutfitData("Partly Cloudy", "Spring", "Canvas Slip-ons", "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=600&q=80"),
                createOutfitData("Partly Cloudy", "Fall", "Light Cardigan", "https://images.unsplash.com/photo-1620799140188-3b2a02fd9a77?w=600&q=80"),

                // --- CLOUDY (5) ---
                createOutfitData("Cloudy", "Fall", "Oversized Hoodie", "https://images.unsplash.com/photo-1556821840-3a63f95609a7?w=600&q=80"),
                createOutfitData("Cloudy", "Fall", "Cargo Pants", "https://images.unsplash.com/photo-1517423568366-8b83523034fd?w=600&q=80"),
                createOutfitData("Cloudy", "Fall", "Denim Jacket", "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=600&q=80"),
                createOutfitData("Cloudy", "Fall", "Chunky Boots", "https://images.unsplash.com/photo-1608256246200-53e635b5b65f?w=600&q=80"),
                createOutfitData("Cloudy", "Spring", "Flannel Shirt", "https://images.unsplash.com/photo-1589310243389-96a5483213a8?w=600&q=80"),

                // --- RAIN (5) ---
                createOutfitData("Rain", "Fall", "Yellow Raincoat", "https://images.unsplash.com/photo-1548883354-7622d03aca27?w=600&q=80"),
                createOutfitData("Rain", "Fall", "Waterproof Boots", "https://images.unsplash.com/photo-1542840410-3092f99611a3?w=600&q=80"),
                createOutfitData("Rain", "Spring", "Compact Umbrella", "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?w=600&q=80"),
                createOutfitData("Rain", "Fall", "Water-Resistant Trench", "https://images.unsplash.com/photo-1591369822096-ffd140ec948f?w=600&q=80"),
                createOutfitData("Rain", "Fall", "Dark Denim Jeans", "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=600&q=80"),

                // --- SNOW (5) ---
                createOutfitData("Snow", "Winter", "Heavy Puffer Jacket", "https://images.unsplash.com/photo-1544923246-77307dd654cb?w=600&q=80"),
                createOutfitData("Snow", "Winter", "Thermal Beanie", "https://images.unsplash.com/photo-1576871337622-98d48d1cf531?w=600&q=80"),
                createOutfitData("Snow", "Winter", "Insulated Snow Boots", "https://images.unsplash.com/photo-1542840410-3092f99611a3?w=600&q=80"),
                createOutfitData("Snow", "Winter", "Wool Knit Scarf", "https://images.unsplash.com/photo-1520903920250-16f5a622c043?w=600&q=80"),
                createOutfitData("Snow", "Winter", "Fleece-Lined Leggings", "https://images.unsplash.com/photo-1506152983158-b4a74a01c721?w=600&q=80")
            )

            outfits.forEach { data ->
                Outfits.insert {
                    it[id] = UUID.randomUUID().toString()
                    it[userId] = systemUserId
                    it[imageUrl] = data.imageUrl
                    it[title] = data.title
                    it[weatherCondition] = data.weatherCondition
                    it[season] = data.season
                    it[likes] = (10..100).random()
                    it[createdAt] = Instant.now() // Outfits uses Instant based on your previous code
                }
            }

            println("Successfully seeded ${outfits.size} outfits.")
        }
    }

    private fun createOutfitData(weather: String, season: String, title: String, url: String): OutfitSeedData {
        return OutfitSeedData(weather, season, title, url)
    }

    private data class OutfitSeedData(
        val weatherCondition: String,
        val season: String,
        val title: String,
        val imageUrl: String
    )
}