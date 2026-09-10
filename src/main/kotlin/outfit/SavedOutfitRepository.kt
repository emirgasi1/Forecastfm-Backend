package com.example.outfit

import com.example.database.table.Outfits
import com.example.database.table.SavedOutfits
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

class SavedOutfitRepository {

    fun saveOutfit(userId: String, outfitId: String) {
        transaction {
            SavedOutfits.insert {
                it[SavedOutfits.userId] = userId
                it[SavedOutfits.outfitId] = outfitId
            }
        }
    }

    fun unsaveOutfit(userId: String, outfitId: String) {
        transaction {
            SavedOutfits.deleteWhere {
                (SavedOutfits.userId eq userId) and (SavedOutfits.outfitId eq outfitId)
            }
        }
    }

    fun isOutfitSaved(userId: String, outfitId: String): Boolean {
        return transaction {
            SavedOutfits
                .selectAll()
                .where {
                    (SavedOutfits.userId eq userId) and (SavedOutfits.outfitId eq outfitId)
                }
                .any()
        }
    }

    fun getSavedOutfits(userId: String): List<Outfit> {
        return transaction {
            (SavedOutfits innerJoin Outfits)
                .selectAll()
                .where { SavedOutfits.userId eq userId }
                .map { row ->
                    Outfit(
                        id = row[Outfits.id],
                        userId = row[Outfits.userId],
                        imageUrl = row[Outfits.imageUrl],
                        title = row[Outfits.title],
                        weatherCondition = row[Outfits.weatherCondition],
                        season = row[Outfits.season],
                        likes = row[Outfits.likes],
                        createdAt = row[Outfits.createdAt].toString()
                    )
                }
        }
    }
}