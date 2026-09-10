package com.example.outfit

import com.example.database.table.Outfits
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.jdbc.update
import java.time.Instant
import java.util.UUID

class OutfitRepository {

    fun createOutfit(
        userId: String,
        imageUrl: String,
        title: String,
        weatherCondition: String,
        season: String,
        storeName: String? = null,
        storeAddress: String? = null,
        price: String? = null
    ): Outfit {
        val id = UUID.randomUUID().toString()
        val createdAt = Instant.now().toString()

        transaction {
            Outfits.insert {
                it[Outfits.id] = id
                it[Outfits.userId] = userId
                it[Outfits.imageUrl] = imageUrl
                it[Outfits.title] = title
                it[Outfits.weatherCondition] = weatherCondition
                it[Outfits.season] = season
                it[Outfits.storeName] = storeName
                it[Outfits.storeAddress] = storeAddress
                it[Outfits.price] = price
                it[Outfits.createdAt] = Instant.now()
            }
        }

        return Outfit(
            id = id,
            userId = userId,
            imageUrl = imageUrl,
            title = title,
            weatherCondition = weatherCondition,
            season = season,
            likes = 0,
            storeName = storeName,
            storeAddress = storeAddress,
            price = price,
            createdAt = createdAt
        )
    }

    fun getTrendingOutfits(limit: Int = 10): List<Outfit> {
        return transaction {
            Outfits
                .selectAll()
                .orderBy(Outfits.likes to SortOrder.DESC)
                .limit(limit)
                .map { row ->
                    Outfit(
                        id = row[Outfits.id],
                        userId = row[Outfits.userId],
                        imageUrl = row[Outfits.imageUrl],
                        title = row[Outfits.title],
                        weatherCondition = row[Outfits.weatherCondition],
                        season = row[Outfits.season],
                        likes = row[Outfits.likes],
                        storeName = row[Outfits.storeName],
                        storeAddress = row[Outfits.storeAddress],
                        price = row[Outfits.price],
                        createdAt = row[Outfits.createdAt].toString()
                    )
                }
        }
    }

    fun getOutfitsByWeather(weather: String, limit: Int = 5): List<Outfit> {
        return transaction {
            Outfits
                .selectAll()
                .where { Outfits.weatherCondition eq weather }
                .orderBy(Outfits.likes to SortOrder.DESC)
                .limit(limit)
                .map { row ->
                    Outfit(
                        id = row[Outfits.id],
                        userId = row[Outfits.userId],
                        imageUrl = row[Outfits.imageUrl],
                        title = row[Outfits.title],
                        weatherCondition = row[Outfits.weatherCondition],
                        season = row[Outfits.season],
                        likes = row[Outfits.likes],
                        storeName = row[Outfits.storeName],
                        storeAddress = row[Outfits.storeAddress],
                        price = row[Outfits.price],
                        createdAt = row[Outfits.createdAt].toString()
                    )
                }
        }
    }

    fun getOutfitById(id: String): Outfit? {
        return transaction {
            Outfits
                .selectAll()
                .where { Outfits.id eq id }
                .singleOrNull()
                ?.let { row ->
                    Outfit(
                        id = row[Outfits.id],
                        userId = row[Outfits.userId],
                        imageUrl = row[Outfits.imageUrl],
                        title = row[Outfits.title],
                        weatherCondition = row[Outfits.weatherCondition],
                        season = row[Outfits.season],
                        likes = row[Outfits.likes],
                        storeName = row[Outfits.storeName],
                        storeAddress = row[Outfits.storeAddress],
                        price = row[Outfits.price],
                        createdAt = row[Outfits.createdAt].toString()
                    )
                }
        }
    }

    fun likeOutfit(outfitId: String) {
        transaction {
            Outfits.update({ Outfits.id eq outfitId }) {
                it[Outfits.likes] = Outfits.likes + 1
            }
        }
    }
}